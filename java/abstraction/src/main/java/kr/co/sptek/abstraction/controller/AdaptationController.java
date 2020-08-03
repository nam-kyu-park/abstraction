package kr.co.sptek.abstraction.controller;

import kr.co.sptek.abstraction.common.DcimTopicId;
import kr.co.sptek.abstraction.kafka.topic.AuthorizationTopic;
import kr.co.sptek.abstraction.service.handler.AuthorizationHandler;
import kr.co.sptek.abstraction.service.request.KafkaRequest;
import kr.co.sptek.abstraction.service.response.KafkaResponse;
import kr.co.sptek.abstraction.util.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class AdaptationController {

    private static final Logger logger = LogManager.getLogger(TestController.class);

    @Autowired
    AuthorizationHandler authorizationHandler;

    @Autowired
    KafkaRequest kafkaRequest;

    @Autowired
    KafkaResponse kafkaResponse;

    @ResponseBody
    @RequestMapping(value = "/agent-measure", method = RequestMethod.POST)
    public String measure(@RequestBody String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_MONITORING, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-measure API: TOPIC_MONITORING: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-alarm", method = RequestMethod.POST)
    public String alarm(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_ALARM, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-alarm API: TOPIC_ALARM: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-request-remote", method = RequestMethod.POST)
    public void requestRemote(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/agent-request-recovery", method = RequestMethod.POST)
    public void requestRecovery(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/agent-recovery", method = RequestMethod.POST)
    public String recovery(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_HEALING, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-recovery API: TOPIC_HEALING: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-request-heartbeat", method = RequestMethod.POST)
    public String requestHeartbeat(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_HEALING, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-request-heartbeat API: TOPIC_HEALING: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-heartbeat", method = RequestMethod.POST)
    public void heartbeat(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/agent-request-auto-recovery", method = RequestMethod.POST)
    public String requestAutoRecovery(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_HEALING, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-request-auto-recovery API: TOPIC_HEALING: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-request-restart", method = RequestMethod.POST)
    public String requestRestart(@RequestParam String json) throws IOException {
        try {
            String response = kafkaResponse.receiveMessage(DcimTopicId.TOPIC_RESTART);
            return response;
        } catch (Exception e) {
            logger.warn("/agent-request-restart API: TOPIC_RESTART: Response process failed.");
        }
        return "failed.";
    }

// TODO: DeployService 에서 배포 토픽 요청을 처리함
//    @ResponseBody
//    @RequestMapping(value = "/agent-request-deploy", method = RequestMethod.POST)
//    public void requestDeploy(@RequestParam String json) throws IOException {
//    }

    @ResponseBody
    @RequestMapping(value = "/agent-deploy", method = RequestMethod.POST)
    public String deploy(@RequestBody String json) {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_DEPLOYSTATUS, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/agent-deploy API: TOPIC_DEPLOYSTATUS: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/agent-authorization", method = RequestMethod.POST)
    public String authorization(@RequestBody String json) {
        try {
            AuthorizationTopic topic = AuthorizationTopic.deserializeJSON(json);
            if (topic != null) {
                boolean authorization = authorizationHandler.authorization(topic);
                topic.status = String.valueOf(authorization ? 1 : 0);
                topic.timestamp = Timestamp.currentTime();
            }
            return topic.serializeJSON();
        } catch (Exception e) {
            logger.warn("/agent-authorization API: TOPIC_AUTHORIZATION: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/mdc-request-pod-relocation", method = RequestMethod.POST)
    public String requestPodRelocation(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_RELOCATION, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/mdc-request-pod-relocation API: TOPIC_RELOCATION: Response process failed.");
        }
        return "failed.";
    }

    @ResponseBody
    @RequestMapping(value = "/mdc-pod-relocation", method = RequestMethod.POST)
    public void podRelocation(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/docker-request-backup-image", method = RequestMethod.POST)
    public void requestBackupImage(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/docker-request-download-image", method = RequestMethod.POST)
    public void requestDownloadImage(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/docker-request-create-image", method = RequestMethod.POST)
    public void requestCreateImage(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/docker-image", method = RequestMethod.POST)
    public void dockerImage(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/mdc-request-pod-shutdown", method = RequestMethod.POST)
    public void requestPodShutdown(@RequestParam String json) throws IOException {
    }

    @ResponseBody
    @RequestMapping(value = "/mdc-pod-shutdown", method = RequestMethod.POST)
    public String podShutdown(@RequestParam String json) throws IOException {
        try {
            kafkaRequest.sendMessage(DcimTopicId.TOPIC_ORCHESTRATION, json);
            return "completed.";
        } catch (Exception e) {
            logger.warn("/mdc-pod-shutdown API: TOPIC_ORCHESTRATION: Response process failed.");
        }
        return "failed.";
    }
}
