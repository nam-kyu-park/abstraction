package kr.co.sptek.abstraction.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class JSONLoader {

    private static final Logger logger = LogManager.getLogger(JSONLoader.class);

    private String path;
    private JSONParser parser;
    private JSONObject json;

    public JSONLoader() {
        this.parser = new JSONParser();
    }

    public JSONLoader(final String path) {
        this.path = path;
        this.parser = new JSONParser();
    }

    public void load() {
        load(this.path);
    }

    public void load(final String path) {
        logger.info("call load [path=" + path + "]");
        if(this.parser == null)
            this.parser = new JSONParser();
        try {
            this.json = (JSONObject)parser.parse(new FileReader(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        try {
            return  this.json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getObject() {
        return this.json;
    }

    public Map<String, Object> toMap() throws IOException {
        Map<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(this.json.toJSONString(), Map.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


}
