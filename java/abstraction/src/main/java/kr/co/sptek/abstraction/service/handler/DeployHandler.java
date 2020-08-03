package kr.co.sptek.abstraction.service.handler;

import kr.co.sptek.abstraction.repos.DeploymentExecutor;
import kr.co.sptek.abstraction.repos.ReposAttribute;
import kr.co.sptek.abstraction.repos.ReposDownloader;
import kr.co.sptek.abstraction.repos.ReposUploader;
import kr.co.sptek.abstraction.module.device.DeviceYaml;
import kr.co.sptek.abstraction.kafka.topic.DeployTopic;
import kr.co.sptek.abstraction.module.shell.ShellCommander;
import kr.co.sptek.abstraction.properties.ResourceProperties;
import kr.co.sptek.abstraction.properties.TestReposProperties;
import kr.co.sptek.abstraction.properties.device.Device;
import kr.co.sptek.abstraction.properties.device.DeviceEntry;
import kr.co.sptek.abstraction.properties.device.DeviceProperties;
import kr.co.sptek.abstraction.service.KafkaService;
import kr.co.sptek.abstraction.util.PathUtil;
import kr.co.sptek.abstraction.util.Timestamp;
import kr.co.sptek.abstraction.util.Zipper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Deprecated
@Component
public class DeployHandler extends KafkaService {

    private static final Logger logger = LogManager.getLogger(DeployHandler.class);

    private static final String TAR_GZIP_SUFFIX = ".tar.gz";


    @Autowired
    ReposDownloader reposDownloader;

    @Autowired
    ReposUploader reposUploader;

    @Autowired
    DeploymentExecutor deploymentExecutor;

    @Autowired
    ReposAttribute reposAttribute;

    @Autowired
    ResourceProperties resource;

    @Autowired
    TestReposProperties testReposProperties;

    @Autowired
    DeviceProperties deviceProperties;

    @Autowired
    AuthorizationHandler authorizationHandler;

    private boolean bValid;
    private ShellCommander shell;
    private DeployTopic topic;
    private String activateCode;

    public DeployHandler() {
        shell = new ShellCommander();
        bValid = false;
    }

    public boolean useRepository(String path) {
        reposUploader.useRepos("dcim-repos");
        reposUploader.mkdirs(resource.deployPath());
        reposUploader.mkdirs(resource.sourcePath());
        reposUploader.mkdirs(resource.sourcePath(topic.company_id));

        //reposUploader.useRepos("mdc-repos");
        for (String device : this.topic.mdc_id) {
            reposUploader.useRepos(device);
            reposUploader.mkdirs(resource.deployPath());
        }
        PathUtil.mkdirs(resource.absolutePath(""));
        PathUtil.mkdirs(resource.absolutePath(topic.company_id));
        return true;
    }

    public void target(DeployTopic topic) {
        this.topic = topic;
    }

    public void download() {
        logger.info("Download agent resource.");

        reposDownloader.useRepos("nas-repos");
        reposDownloader.download(resource.getAgent(), resource.absolutePath(topic.company_id));
        reposDownloader.download(resource.getCollector(), resource.absolutePath(topic.company_id));

        String agentPath = resource.absolutePath(topic.company_id, resource.agentName());
        String collectorPath = resource.absolutePath(topic.company_id, resource.collectorName());
        reposUploader.useRepos("dcim-repos");
        reposUploader.upload(agentPath, resource.sourcePath(topic.company_id));
        reposUploader.upload(collectorPath, resource.sourcePath(topic.company_id));

        // create local directory path
        // host entry 은 추후 application.yml에서 분리하여 dcim-repository에 배포 예정
        // host entry yaml 데이터 파싱 기능 필요 (예정)
        logger.info("Download hosts resource");
        reposDownloader.useRepos("dcim-repos");
        reposDownloader.download(resource.getHosts(), resource.absolutePath());
        reposDownloader.download(resource.getAdaptation(), resource.absolutePath(topic.company_id));

    }

    public void activate(String device) throws IOException {
        activateCode = UUID.randomUUID().toString().toUpperCase();
        logger.info("Create Activation code: " + activateCode);

        // create activate code
        String activeFile = resource.activePath(topic.company_id);
        writeActivateCode(activateCode, activeFile);

        // copy device information to repository
        String hostsFile = resource.hostPath(topic.company_id);
        writeHostCode(this.topic.device_id, hostsFile);

        // copy activate code to repository
        String dest = resource.sourcePath(topic.company_id);
        reposUploader.useRepos("dcim-repos");
        reposUploader.upload(activeFile, dest);
        reposUploader.upload(hostsFile, dest);

        // Insert expiry date
        try {
            String startDate = Timestamp.currentTime();
            String endDate = Timestamp.yearExpiration(startDate);
            authorizationHandler.insertExpiryDate(topic.company_id, device, activateCode, startDate, endDate);
        }
        catch (Exception e) {
            logger.warn("Registry expiry date of activate code failed. [company=" + topic.company_id + ", device=" + device + "]");
        }
    }

    public void writeActivateCode(String code, String path) throws IOException  {
        logger.info("Write Activation code to file: " + path);
        FileOutputStream out1 = new FileOutputStream(new File(path));
        out1.write(activateCode.getBytes());
        out1.close();
    }

    public void writeHostCode(List<String> devices, String path) throws IOException  {
        logger.info("Write host code to file: " + path);
        DeviceEntry entry = new DeviceEntry(deviceProperties.getDevice());
        DeviceYaml yaml = new DeviceYaml();
        for(String id : devices) {
            Device device = entry.getDevice(entry.indexOf(id));
            yaml.add(device);
        }
        yaml.write(path);
    }

    public void compress(String device) throws IOException {
        logger.info("Host resource compress.");

        String dest = resource.sourcePath(topic.company_id) + "/*";
        String local = resource.absolutePath(topic.company_id);
        String archive = local + "-" + device + TAR_GZIP_SUFFIX;

        reposDownloader.useRepos("dcim-repos");
        reposDownloader.download(dest, local);

        Zipper.compress(archive, local, ArchiveFormat.TAR, CompressionType.GZIP);
        Zipper.decompress(archive, testReposProperties.getWorkspace(), ArchiveFormat.TAR, CompressionType.GZIP);

        reposUploader.useRepos("dcim-repos");
        reposUploader.upload(archive, resource.deployPath());
    }

    public void deploy(String device) {
        logger.info("Distribute agents to destinations.");

        String dest = resource.deployPath(topic.company_id) + "-" + device;
        String local = resource.absolutePath(topic.company_id) + "-" + device;

        reposDownloader.useRepos("dcim-repos");
        reposDownloader.download(dest + TAR_GZIP_SUFFIX, local);

        reposUploader.useRepos(device);
        reposUploader.upload(local + TAR_GZIP_SUFFIX, resource.deployPath());

//        deploymentExecutor.useRepos(device);
//        deploymentExecutor.mkdirs(dest);
//        deploymentExecutor.decompress(local + TAR_GZIP_SUFFIX, dest, false);
//        deploymentExecutor.permission("0755", dest + "/" + resource.agentName(), dest + "/" + resource.collectorName());
//        deploymentExecutor.execute("", dest + "/" + resource.collectorName(), "");
    }

    public void flush() {
        logger.info("Host handling flush.");

        reposUploader.useRepos("nas-repos");
        reposUploader.flush();

        reposUploader.useRepos("dcim-repos");
        reposUploader.flush();

        for (String device : this.topic.mdc_id) {
            reposUploader.useRepos(device);
            reposUploader.flush();
        }
    }
}