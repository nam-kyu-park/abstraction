package kr.co.sptek.abstraction.db.mapper;

import kr.co.sptek.abstraction.common.DcimDeviceId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Configuration
@ConfigurationProperties(value="dcim.source.device")
public class DeviceMapper {

    private static final Logger logger = LogManager.getLogger(DeviceMapper.class);

    private String jsonVersion;
    private String filePath;
    private Resource resource;
    private Map<String, Object> map;
    private Map<String, String> nameMap;
    private Map<String, String> displayMap;

    @Autowired
    ResourceLoader resourceLoader;

    public DeviceMapper() {
        this.nameMap = new HashMap<>();
        this.displayMap = new HashMap<>();
    }

    public String toString() {
        return "DeviceMapper [jsonVersion=" + jsonVersion + ", filePath=" + filePath + "]";
    }

    public String getJsonVersion() {
        return jsonVersion;
    }

    public void setJsonVersion(String jsonVersion) {
        this.jsonVersion = jsonVersion;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean load() throws Exception {
        this.resource = resourceLoader.getResource(this.filePath);
        String path = this.resource.getURI().getPath().substring(1);
        JSONArray arr = DeviceMapper.deserializeJSON(path);
        for(Object o: arr){
            if ( o instanceof JSONObject ) {
                String c = (String)((JSONObject) o).get(DcimDeviceId.DEVICE_CODE);
                String n = (String)((JSONObject) o).get(DcimDeviceId.DEVICE_NAME);
                this.nameMap.put(c, n);
                String d = (String)((JSONObject) o).get(DcimDeviceId.DEVICE_DISPLAY);
                this.displayMap.put(c, d);
            }
        }
        return exists();
    }

    public boolean exists() throws Exception {
        return this.nameMap.isEmpty() && this.displayMap.isEmpty();
    }

    public String getName(String code) throws Exception {
        return nameMap.get(code);
    }

    public String getDisplay(String code) throws Exception {
        return displayMap.get(code);
    }

    public static JSONArray deserializeJSON(final String path) throws ParseException, IOException {
        logger.info("call deserializeJSON String topic");
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject)parser.parse(new FileReader(path));
        return (JSONArray)object.get(DcimDeviceId.DEVICE_ENTRY);
    }
}
