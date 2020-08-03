package kr.co.sptek.abstraction.kafka.topic;

import kr.co.sptek.abstraction.common.DcimTopicId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeployStatusTopic {

    private static final Logger logger = LogManager.getLogger(DeployStatusTopic.class);

    public String company_id;
    public String mdc_id;
    public List<String> device_id;
    public Map<String, String> status;
    public String timestamp;


    public String toString() {
        return "DeployTopic [ company_id=" + company_id + ", mdc_id=" + mdc_id  + ", device_id=" + device_id
                + ", status=" + status + ", timestamp=" + timestamp + " ]";
    }

    public static DeployStatusTopic deserializeJSON(final String topic) throws IOException {
        logger.info("call deserializeJSON: String topic");
        try {
            JSONParser parser = new JSONParser();
            return deserializeJSON((JSONObject)parser.parse(topic));
        } catch (Exception e) {
            logger.warn("Host topic deserialize failed.");
        }
        return null;
    }


    public static DeployStatusTopic deserializeJSON(final JSONObject topic) throws IOException {
        JSONObject obj = (JSONObject)topic.get(DcimTopicId.TOPIC_DEPLOYSTATUS);
        if(obj != null) {
            logger.info("call deserializeJSON: JSONObject topic");
            DeployStatusTopic data = new DeployStatusTopic();
            data.company_id = (String)obj.get("company_id");
            data.mdc_id = (String)obj.get("mdc_id");
            data.device_id = deserializeJSONArray(obj.get("device_id"));
            data.status =(Map<String, String>)obj.get("status");
            data.timestamp = (String)obj.get("timestamp");
            return data;
        }
        return null;
    }

    public static JSONObject serializeJSONObject(final DeployStatusTopic data) throws IOException {
        JSONObject c = new JSONObject();
        c.put("company_id", data.company_id);
        c.put("mdc_id", data.mdc_id);
        c.put("device_id", data.device_id);
        c.put("status", data.status);
        c.put("timestamp", data.timestamp);

        JSONObject p = new JSONObject();
        p.put(DcimTopicId.TOPIC_DEPLOYSTATUS, c);
        return p;
    }

    public static List<String> deserializeJSONArray(Object  data) throws IOException {
        JSONArray jArr = (JSONArray)data;
        List<String> list = new ArrayList<>();
        for (Object str : jArr.toArray()) {
            list.add((String)str);
        }
        return list;
    }

    public static String serializeJSON(final DeployStatusTopic data) throws IOException {
        return DeployStatusTopic.serializeJSONObject(data).toString();
    }

    public String serializeJSON() throws IOException {
        return DeployStatusTopic.serializeJSONObject(this).toString();
    }
}
