package kr.co.sptek.abstraction.module.device;

import kr.co.sptek.abstraction.db.mapper.YamlMapper;
import kr.co.sptek.abstraction.properties.device.Device;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class DeviceYaml {

    private static final Logger logger = LogManager.getLogger(DeviceYaml.class);
    private DumperOptions options;
    private List<Device> devices;

    public DeviceYaml() {
        this.options = new DumperOptions();
        this.devices = new LinkedList<>();
        this.options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setExplicitStart(true);
    }

    public DeviceYaml(boolean prettyFlow, boolean explicitStart) {
        this.options = new DumperOptions();
        this.devices = new LinkedList<>();
        this.options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(prettyFlow);
        options.setExplicitStart(explicitStart);
    }

    public DeviceYaml(DumperOptions options) {
        this.options = new DumperOptions();
        this.devices = new LinkedList<>();
        this.options = options;
    }

    public DumperOptions getOptions() {
        return options;
    }

    public void setOptions(DumperOptions options) {
        this.options = options;
    }

    public void add(Device device) {
        this.devices.add(device);
    }

    public void write(String path) {
        logger.info("call write ansible object to " + path);

        YamlMapper.YamlBuilder builder = new YamlMapper.YamlBuilder();
        for(Device item : devices) {
            YamlMapper mapper = new YamlMapper.YamlBuilder()
                    .map("name", item.getName())
                    .map("host", item.getHost())
                    .map("type", item.getType())
                    .map("control", item.getControl())
                    .map("display", item.getDisplay())
                    .build();

            builder = builder.sequences(mapper);
        }
        YamlMapper deviceSeq = builder.build();

        YamlMapper deviceMap = new YamlMapper.YamlBuilder()
                .map("device", deviceSeq)
                .build();

        YamlMapper mdcMap = new YamlMapper.YamlBuilder()
                .map("mdc", deviceMap)
                .build();

        Yaml yaml = new Yaml(this.options);
        StringWriter writer = new StringWriter();
        yaml.dump(mdcMap.getMap(), writer);

        try {
            FileOutputStream out = new FileOutputStream(new File(path));
            out.write(writer.toString().getBytes());
            out.close();
        }

        catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        }

        catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }
}
