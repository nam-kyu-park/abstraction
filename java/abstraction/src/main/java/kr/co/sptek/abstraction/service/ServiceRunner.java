package kr.co.sptek.abstraction.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.repos.ReposDownloader;
import kr.co.sptek.abstraction.repos.ReposUploader;
import kr.co.sptek.abstraction.db.dto.AuthorizationDto;
import kr.co.sptek.abstraction.module.ansible.Ansible;
import kr.co.sptek.abstraction.module.ansible.AnsibleYaml;
import kr.co.sptek.abstraction.module.ftp.FTPConnector;
import kr.co.sptek.abstraction.db.mapper.AuthorizationMapper;
import kr.co.sptek.abstraction.module.stream.chananel.KafkaChannel;
import kr.co.sptek.abstraction.module.stream.connector.KafkaConnector;
import kr.co.sptek.abstraction.properties.*;
import kr.co.sptek.abstraction.properties.device.Device;
import kr.co.sptek.abstraction.properties.device.DeviceEntry;
import kr.co.sptek.abstraction.properties.device.DeviceProperties;
import kr.co.sptek.abstraction.service.request.KafkaRequest;
import kr.co.sptek.abstraction.service.response.KafkaResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//@Component
//public class ServiceRunner implements ApplicationListener<ContextStartedEvent> {
//    @Override
//    public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
//        // ...
//    }
//}

@Component
public class ServiceRunner implements ApplicationRunner {

    private static final Logger logger = LogManager.getLogger(ServiceRunner.class);

    KafkaChannel defaultChannel;
    KafkaChannel alarmChannel;
    KafkaChannel deployChannel;

    @Autowired
    KafkaConnector connector;

    @Autowired
    KafkaRequest kafkaRequest;

    @Autowired
    KafkaResponse kafkaResponse;

    @Autowired
    DeployService deployService;

    @Autowired
    ResourceProperties reposProperties;

    public void ServiceRunner() {
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        service();

//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
//        test7();
//        test8();

    }

    public void service() throws Exception  {
        try {
            defaultChannel = new KafkaChannel(connector, "Kafka defualt channel");
            defaultChannel.open();
            kafkaRequest.setProxy(defaultChannel.inbound(), ProxyRules.INBOUND);
            kafkaResponse.setProxy(defaultChannel.outbound(), ProxyRules.OUTBOUND);

            deployChannel = new KafkaChannel(connector, "Kafka deploy channel");
            deployChannel.open();
            deployService.setProxy(deployChannel.inbound(), ProxyRules.INBOUND);
            deployService.setProxy(deployChannel.outbound(), ProxyRules.OUTBOUND);

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    public void test1() throws Exception  {

        logger.info("call test1 function.");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setExplicitStart(true);

        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();

        Map<Object, Object> dir = new LinkedHashMap<>();
        dir.put("name", "PyYAML");
        dir.put("status", "4");
        dir.put("license", "MIT");
        dir.put("language", "Python");

        Map<Object, Object> copy = new LinkedHashMap<>();
        copy.put("name", "PySyck");
        copy.put("s", "5");
        copy.put("license", "BSD");
        copy.put("language", "Python");

        ArrayList<Object> tasks = new ArrayList<>();
        tasks.add(dir);
        tasks.add(copy);

        Map<Object, Object> play = new LinkedHashMap<>();
        play.put("hosts", "default");
        play.put("tasks", tasks);

        ArrayList<Object> book = new ArrayList<>();
        book.add(play);

        yaml.dump(book, writer);

        File file = new File("D:\\tmp\\test\\test-main.yml");
        FileOutputStream out = new FileOutputStream(file);
        out.write(writer.toString().getBytes());
        out.close();
    }

    public void test2() throws Exception  {

        logger.info("call test2 function.");

        Ansible createDir = new Ansible.AnsibleBuilder()
                .put("path", "/temp")
                .put("state", "directory")
                .put("mode", "0755")
                .build();

        Ansible copyFile = new Ansible.AnsibleBuilder()
                .put("dest", "/temp")
                .put("src", "/temp")
                .put("mode", "0644")
                .build();

        Ansible createTask = new Ansible.AnsibleBuilder()
                .put("name", "Create directory")
                .sub("file", createDir)
                .build();

        Ansible copyTask = new Ansible.AnsibleBuilder()
                .put("name", "Copy file")
                .sub("copy", copyFile)
                .build();

        Ansible ansibleTasks = new Ansible.AnsibleBuilder()
                .sub(createTask)
                .sub(copyTask)
                .sub("include", "test1.yml")
                .sub("include", "test2.yml")
                .sub("include", "test3.yml")
                .build();

        Ansible document = new Ansible.AnsibleBuilder()
                .put("hosts", "default")
                .put("tasks", ansibleTasks.sequence())
                .build();

        AnsibleYaml yaml = new AnsibleYaml();
        yaml.write(document, "d:/tmp/test/test-main-02.yml");

    }



    @Autowired
    ReposUploader reposUploader;

    @Autowired
    ReposDownloader reposDownloader;

    public void test3() throws Exception  {
        if(reposDownloader.useRepos("nas-repos")) {
            reposDownloader.download("release/1.0/dcim-agent-v1.0", "D:/tmp/test/out");
            reposUploader.upload("D:/tmp/test/out/dcim-agent-v2.0", "release/2.0");
        }

        if(reposUploader.useRepos("dcim-repos")) {
            reposDownloader.download("test/dcim-agent-v1.0", "D:/tmp/test/out");
            reposUploader.upload("D:/tmp/test/out/dcim-agent-v2.0", "test");
        }

        if(reposUploader.useRepos("mdc-repos")) {
            reposDownloader.download("", "");
            reposUploader.upload("D:/tmp/test/int/8e1b5d41-dfa9-479e-8ffd-745d7edba27c.tar.gz", "out");
        }
    }

    public void test4() {
        String path1 = "/root/repository";
        String path2 = "/repository/agent/ver/2.0/agent-v2.0";
        Path pathBase = Paths.get(path1);
        Path pathAbsolute = Paths.get(path2);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        logger.info(pathRelative.toString());
        logger.info(pathRelative.getFileName().toString());
        logger.info(pathRelative.getParent().toString());
        logger.info(pathRelative.getFileSystem().toString());
    }

    public void test5() {
         Ansible copy = new Ansible.AnsibleBuilder()
                .put("src", "test1")
                .put("dest", "test2")
                .put("mode", "0644")
                .build();

        Ansible task = new Ansible.AnsibleBuilder()
                .put("name", "Upload files to remote systems.")
                .put("copy", copy.element())
                .build();

        try {
            //AnsibleYaml yaml = new AnsibleYaml(false, false, false);
            AnsibleYaml yaml = new AnsibleYaml();
            yaml.write(task, "d:/tmp/test/ansible-copy.yml");
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public void test6() throws JSchException, SftpException {
        FTPConnector connector = new FTPConnector();
        connector.init("210.217.178.129", "22", "root", "Sptek12#$");
        connector.mkdir("/root/repos/mdc/" + reposProperties.deployPath("test"));
        connector.stat(reposProperties.deployPath("test"));
    }

    @Autowired
    DeviceProperties deviceProperties;
    public void test7(){
        DeviceEntry entry = new DeviceEntry(deviceProperties.getDevice());
        logger.info(entry.toString());
        int index = entry.indexOf("a947db95-0536-4cd3-b17e-9ee9813e926f");
        Device device = entry.getProperties().get(index);
        logger.info(device.toString());
    }

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    AuthorizationMapper authorizationMapper;

    public void test8(){
        logger.info(databaseProperties.toString());

        try {
            AuthorizationDto dto = new AuthorizationDto();
            dto.setCompany("8e1b5d41-dfa9-479e-8ffd-745d7edba27c");
            dto.setMdc("3369373b-2353-4983-abe9-8bca18204892");
            dto.setCode("E424C6AF-5979-4E1C-A1C4-1CBB587DD497");
            logger.info("prameter: " + dto.toString());

            //AuthorizationDto rst = authorizationMapper.selectExpiryDate(dto);
            //logger.info("result: " + rst.toString());

            AuthorizationDto rst = authorizationMapper.selectExpiryDate(
                    "8e1b5d41-dfa9-479e-8ffd-745d7edba27c",
                    "3369373b-2353-4983-abe9-8bca18204892",
                    "E424C6AF-5979-4E1C-A1C4-1CBB587DD497");
            logger.info("result: " + rst.toString());
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

}

