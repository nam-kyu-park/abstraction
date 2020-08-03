package kr.co.sptek.abstraction.module.ansible;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

public class AnsibleYaml {

    private static final Logger logger = LogManager.getLogger(AnsibleYaml.class);
    private boolean prettyFlow;
    private boolean explicitStart;
    private boolean sequence;

    public AnsibleYaml() {
        this.prettyFlow = true;
        this.explicitStart = true;
        this.sequence = true;
    }

    public AnsibleYaml(boolean prettyFlow, boolean explicitStart, boolean sequence) {
        this.prettyFlow = prettyFlow;
        this.explicitStart = explicitStart;
        this.sequence = sequence;
    }

    public void write(Ansible as, String path) throws IOException {

        logger.info("call write ansible object to " + path);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(this.prettyFlow);
        options.setExplicitStart(this.explicitStart);

        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();
        if(this.sequence)
            yaml.dump(as.sequence(), writer);
        else
            yaml.dump(as.element(), writer);

        // create activate code
        File file = new File(path);
        FileOutputStream out = new FileOutputStream(file);
        out.write(writer.toString().getBytes());
        out.close();
    }
}
