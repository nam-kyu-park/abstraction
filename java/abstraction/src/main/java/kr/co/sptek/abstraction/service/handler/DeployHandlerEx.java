package kr.co.sptek.abstraction.service.handler;

import kr.co.sptek.abstraction.kafka.topic.DeployTopic;
import kr.co.sptek.abstraction.properties.ResourceProperties;
import kr.co.sptek.abstraction.properties.TestReposProperties;
import kr.co.sptek.abstraction.repos.DeploymentExecutor;
import kr.co.sptek.abstraction.repos.ReposAttribute;
import kr.co.sptek.abstraction.repos.ReposDownloader;
import kr.co.sptek.abstraction.repos.ReposUploader;
import kr.co.sptek.abstraction.service.KafkaService;
import kr.co.sptek.abstraction.util.PathUtil;
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

@Component
public class DeployHandlerEx extends KafkaService {
    private static final Logger logger = LogManager.getLogger(DeployHandler.class);

    private static final String TAR_GZIP_SUFFIX = ".tar.gz";
    private DeployTopic topic;
    private String activateCode;

    @Autowired
    ReposDownloader reposDownloader;

    @Autowired
    ReposUploader reposUploader;

    @Autowired
    ResourceProperties resource;

    @Autowired
    TestReposProperties testReposProperties;

    @Autowired
    DeploymentExecutor deploymentExecutor;

    @Autowired
    ReposAttribute reposAttribute;

    public DeployHandlerEx() {
    }

    public boolean useRepository(String path) {
        reposUploader.useRepos("dcim-repos");
        reposUploader.mkdirs(resource.deployPath());
        reposUploader.mkdirs(resource.sourcePath());
        reposUploader.mkdirs(resource.sourcePath(topic.company_id));

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

    public void download(String device) {
        logger.info("Download resource.");
        reposDownloader.useRepos("nas-repos");
        reposDownloader.download(resource.joinPath(resource.getRelease(), device), resource.absolutePath(topic.company_id));

        logger.info("Upload resource.");
        reposUploader.useRepos("dcim-repos");
        reposUploader.upload(resource.absolutePath(topic.company_id), resource.sourcePath(topic.company_id));
    }

    public void activate(String device) throws IOException {
        activateCode = UUID.randomUUID().toString().toUpperCase();
        logger.info("Create Activation code: " + activateCode);

        // create activate code
        String activeFile = resource.activePath(topic.company_id);
        writeActivateCode(activateCode, activeFile);

        // copy activate code to repository
        String dest = resource.sourcePath(topic.company_id);
        reposUploader.useRepos("dcim-repos");
        reposUploader.upload(activeFile, dest);
    }

    public void writeActivateCode(String code, String path) throws IOException  {
        logger.info("Write Activation code to file: " + path);
        FileOutputStream out1 = new FileOutputStream(new File(path));
        out1.write(activateCode.getBytes());
        out1.close();
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

        deploymentExecutor.useRepos(device);
        deploymentExecutor.mkdirs(dest);
        deploymentExecutor.decompress(local + TAR_GZIP_SUFFIX, dest, false);
        deploymentExecutor.permission("0755", dest + "/" + resource.agentName(), dest + "/" + resource.collectorName());


        String cmd, file;

        cmd = String.format("cd %s; sh run-collector.sh", reposAttribute.remote() + "/" + dest);
        deploymentExecutor.execute(cmd);

        cmd = String.format("cd %s; sh run-agent.sh", reposAttribute.remote() + "/" + dest);
        deploymentExecutor.execute(cmd);

//        deploymentExecutor.execute("", dest + "/" + resource.collectorName() + " -i config/collector.yml -l log-collector &", "");
//        deploymentExecutor.execute("", dest + "/" + resource.agentName() + " -i config/agent.yml -l log-agent &", "");
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
