package kr.co.sptek.abstraction.module.ansible;

import kr.co.sptek.abstraction.module.shell.ShellCommander;
import kr.co.sptek.abstraction.properties.Repos.RemoteProperties;
import kr.co.sptek.abstraction.properties.ResourceProperties;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

@Component
public class AnsibleConnector {
    private static final Logger logger = LogManager.getLogger(AnsibleConnector.class);

    private ResourceProperties resourceProperties;
    private List<RemoteProperties> remotePropertiesList;
    private List<String> tasks;
    private String remoteName;
    private int mkdirCount;
    private int uploadCount;
    private int deploymentCount;
    private int executeCount;
    private int templateCount;


    public AnsibleConnector(ResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
        this.remotePropertiesList = new LinkedList<>();
        this.tasks = new LinkedList<>();
        this.mkdirCount = 0;
        this.uploadCount = 0;
        this.deploymentCount = 0;
        this.executeCount = 0;
        this.templateCount = 0;
    }

    public String getHome() {
        return "";
    }

    public void init(String path) {
        logger.info("Initialize ansible connection: [path=" + path + "]");

        remotePropertiesList = loadEntry(path);
        if(!remotePropertiesList.isEmpty()) {
            Ansible.AnsibleBuilder builder = new Ansible.AnsibleBuilder();
            for (RemoteProperties remote : this.remotePropertiesList) {
                Ansible tasks = new Ansible.AnsibleBuilder()
                        .put("ansible_connection", "ssh")
                        .put("ansible_host", remote.getServer())
                        .put("ansible_port", remote.getPort())
                        .put("ansible_user", remote.getUser())
                        .put("ansible_password", remote.getPassword())
                        .build();

                String key = String.format("deploy-%s", remote.getName());
                builder = builder.sub(key, tasks);
            }
            Ansible deploy = builder.build();

            Ansible node3 = new Ansible.AnsibleBuilder()
                    .put("hosts", deploy.element())
                    .build();

            Ansible document = new Ansible.AnsibleBuilder()
                    .put("all", node3.element())
                    .build();

            wirte(document, absolutePath("inventory.yml"),
                    new AnsibleYaml(false, false, false));
        }
    }

    public void init(RemoteProperties remote) {
       logger.info("Initialize ansible connection: ["
               + "host=" + remote.getServer()
               + ", port=" + remote.getPort()
               + ", user=" + remote.getUser()
               + ", password=" + remote.getPassword() +"]" );

        Ansible node1 = new Ansible.AnsibleBuilder()
                .put("ansible_connection", "ssh")
                .put("ansible_host", remote.getServer())
                .put("ansible_port", remote.getPort())
                .put("ansible_user", remote.getUser())
                .put("ansible_password", remote.getPassword())
                .put("ansible_ssh_pass", remote.getPassword())
                .build();

        Ansible node2 = new Ansible.AnsibleBuilder()
                .put("deploy", node1.element())
                .build();

        Ansible node3 = new Ansible.AnsibleBuilder()
                .put("hosts", node2.element())
                .build();

        Ansible document = new Ansible.AnsibleBuilder()
                .put("all", node3.element())
                .build();

        this.remoteName = remote.getName();
        wirte(document, absolutePath(String.format("inventory-%s.yml", this.remoteName)),
                new AnsibleYaml(false, false, false));

    }

    public void mkdir(String dir) {
        logger.info("Create directory: " + dir);

        Ansible node = new Ansible.AnsibleBuilder()
                .put("path", dir)
                .put("state", "directory")
                .put("mode", "0755")
                .build();

        Ansible tasks = new Ansible.AnsibleBuilder()
                .put("name", "Create directory")
                .put("file", node.element())
                .build();

        String file = absolutePath(String.format("mkdir-%s-%d.yml", this.remoteName, this.mkdirCount));
        wirte(tasks, file, new AnsibleYaml());

        add(file);
        this.mkdirCount++;
    }

    public void upload(String dest, String src) {
        logger.info("Upload file to directory: " + src + " --> " + dest);

        Ansible copy = new Ansible.AnsibleBuilder()
                .put("src", src)
                .put("dest", dest)
                .put("mode", "0644")
                .build();

        Ansible tasks = new Ansible.AnsibleBuilder()
                .put("name", "Copy file")
                .put("copy", copy.element())
                .build();

        String file = absolutePath(String.format("copy-%s-%d.yml", this.remoteName, this.uploadCount));
        wirte(tasks, file, new AnsibleYaml());

        add(file);
        this.uploadCount++;
    }

    public void decompress(String dest, String src, boolean remote_src) {
        logger.info("Decompress file to directory: " + src + " --> " + dest);

        Ansible unarchive = new Ansible.AnsibleBuilder()
                .put("src", src)
                .put("dest", dest)
                .put("remote_src", (remote_src) ? "yes" : "no")
                .build();

        Ansible tasks = new Ansible.AnsibleBuilder()
                .put("name", "Unarchive file")
                .put("unarchive", unarchive.element())
                .build();

        String file = absolutePath(String.format("unarchive-%s-%d.yml", this.remoteName, this.deploymentCount));
        wirte(tasks, file, new AnsibleYaml());

        add(file);
        this.deploymentCount++;
    }

    public void permission(String mode, String...files) {

        Ansible.AnsibleBuilder itemBuilder = new Ansible.AnsibleBuilder();
        for(String file : files) {
            itemBuilder.sub(file);
        }
        Ansible items = itemBuilder.build();

        Ansible node = new Ansible.AnsibleBuilder()
                .put("path", "{{ item }}")
                .put("state", "touch")
                .put("mode", mode)
                .build();

        Ansible tasks = new Ansible.AnsibleBuilder()
                .put("name", "Change permission")
                .put("file", node.element())
                .put("with_items", items.sequence())
                .build();

        String file = absolutePath(String.format("permission-%s-%d.yml", this.remoteName, this.templateCount));
        wirte(tasks, file, new AnsibleYaml());

        add(file);
        this.templateCount++;
    }

    public void execute(String task) {
        logger.info("Execute the process: " + task);

        Ansible tasks = new Ansible.AnsibleBuilder()
                .put("name", "Execute the process")
                .put("shell", task)
                .build();

        String file = absolutePath(String.format("shell-%s-%d.yml", this.remoteName, this.executeCount));
        wirte(tasks, file, new AnsibleYaml());

        add(file);
        this.executeCount++;
    }

    public void flush() {
        Ansible.AnsibleBuilder builder = new Ansible.AnsibleBuilder();
        for (String path : this.tasks) {
            builder = builder.sub("include_tasks", path);
        }
        Ansible tasks = builder.build();

        Ansible document = new Ansible.AnsibleBuilder()
                .put("hosts", "all")
                .put("tasks", tasks.sequence())
                .build();

        String book = absolutePath(String.format("main-%s.yml", this.remoteName));
        wirte(document, book, new AnsibleYaml());

        String inventory = absolutePath(String.format("inventory-%s.yml", this.remoteName));
        paly(inventory, book);

        this.tasks.clear();
        this.mkdirCount = 0;
        this.uploadCount = 0;
        this.deploymentCount = 0;
        this.executeCount = 0;
        this.templateCount = 0;
    }

    private void add(String fileName) {
        tasks.add(fileName);
    }

    public void wirte(Ansible task, String fileName, AnsibleYaml yaml) {
        try {
            yaml.write(task, fileName);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }


    public void paly(String inventroy, String book) {
        String play = String.format("ansible-playbook -i %s -f 10 %s ", inventroy, book);
        logger.info(play);

        try {
            ShellCommander shell = new ShellCommander();
            shell.execute(play);
        }
        catch (IOException e) {
            logger.warn(e.getMessage());
        }
        catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }

    private String absolutePath(String path) {
        return FilenameUtils.separatorsToUnix(resourceProperties.absoluteTemporaryPath() + "/" + path);
    }

    public List<RemoteProperties> loadEntry(String path) {
        List<RemoteProperties> properties = new LinkedList<>();

        try {
            Yaml yaml = new Yaml(new Constructor(RemoteProperties.class));
            InputStream inputStream = new FileInputStream(new File(path));
            for (Object object : yaml.loadAll(inputStream))
                properties.add((RemoteProperties)object);
        }
        catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        }

        return properties;
    }
}
