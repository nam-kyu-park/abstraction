package kr.co.sptek.abstraction.controller;

import kr.co.sptek.abstraction.common.DcimTopicId;
import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.kafka.topic.DeployStatusTopic;
import kr.co.sptek.abstraction.module.stream.DPair;
import kr.co.sptek.abstraction.service.response.KafkaResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Autowired
    KafkaResponse kafkaResponse;

    void send(String topic, String message) throws Exception {
        
        if(topic == null || topic.isEmpty())
            throw new Exception();

        if(message == null || message.isEmpty())
            throw new Exception();
        
        kafkaResponse.proxy(ProxyRules.OUTBOUND).send(new DPair<String, String>(topic, message));
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_ORCHESTRATION)
    public void listen1(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_ORCHESTRATION);
        try {
            send(DcimTopicId.TOPIC_ORCHESTRATION, message);
        } catch (Exception e) {
            logger.warn("TOPIC_ORCHESTRATION: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_ALARM)
    public void listen2(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_ALARM);
        try {
            send(DcimTopicId.TOPIC_ALARM, message);
        } catch (Exception e) {
            logger.warn("TOPIC_ALARM: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_REMOTE)
    public void listen3(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_REMOTE);
        try {
            send(DcimTopicId.TOPIC_REMOTE, message);
        } catch (Exception e) {
            logger.warn("TOPIC_REMOTE: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_DEVICE_HANDLER)
    public void listen4(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_DEVICE_HANDLER);
        try {
            send(DcimTopicId.TOPIC_DEVICE_HANDLER, message);
        } catch (Exception e) {
            logger.warn("TOPIC_DEVICE_HANDLER: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_MONITORING_HANDLER)
    public void listen5(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_MONITORING_HANDLER);
        try {
            send(DcimTopicId.TOPIC_MONITORING_HANDLER, message);
        } catch (Exception e) {
            logger.warn("TOPIC_MONITORING_HANDLER: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_DIAGNOSIS)
    public void listen6(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_DIAGNOSIS);
        try {
            send(DcimTopicId.TOPIC_DIAGNOSIS, message);
        } catch (Exception e) {
            logger.warn("TOPIC_DIAGNOSIS: Response kafka send outbound message failed.");
        }
    }

    //  @KafkaListener(topics = DcimTopicId.TOPIC_HEALING)
    public void listen7(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_HEALING);
        try {
            send(DcimTopicId.TOPIC_HEALING, message);
        } catch (Exception e) {
            logger.warn("TOPIC_HEALING: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_VM)
    public void listen8(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_VM);
        try {
            send(DcimTopicId.TOPIC_VM, message);
        } catch (Exception e) {
            logger.warn("TOPIC_VM: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_VDEVICE_HANDLER)
    public void listen9(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_VDEVICE_HANDLER);
        try {
            send(DcimTopicId.TOPIC_VDEVICE_HANDLER, message);
        } catch (Exception e) {
            logger.warn("TOPIC_VDEVICE_HANDLER: Response kafka send outbound message failed.");
        }
    }

//    @KafkaListener(topics = DcimTopicId.TOPIC_DEPLOY)
//    public void listen10(String message) {
//        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_DEPLOY);
//        try {
//            send(DcimTopicId.TOPIC_DEPLOY, message);
//        } catch (Exception e) {
//            logger.warn(": Response kafka send outbound message failed.");
//        }
//    }

    @KafkaListener(topics = DcimTopicId.TOPIC_DEPLOYSTATUS)
    public void listen10(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_DEPLOYSTATUS);
        try {
            DeployStatusTopic topic = DeployStatusTopic.deserializeJSON(message);
            logger.info("Trace: " + topic.toString());
        } catch (Exception e) {
            logger.warn(": Response kafka send outbound message failed.");
        }
    }

//    @KafkaListener(topics = DcimTopicId.TOPIC_AUTHORIZATION)
//    public void listen11(String message) {
//        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_AUTHORIZATION);
//        try {
//            send(DcimTopicId.TOPIC_AUTHORIZATION, message);
//        } catch (Exception e) {
//            logger.warn("TOPIC_AUTHORIZATION: Response kafka send outbound message failed.");
//        }
//    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_RELOCATION)
    public void listen12(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_RELOCATION);
        try {
            send(DcimTopicId.TOPIC_RELOCATION, message);
        } catch (Exception e) {
            logger.warn("TOPIC_RELOCATION: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_DOCKER)
    public void listen13(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_DOCKER);
        try {
            send(DcimTopicId.TOPIC_DOCKER, message);
        } catch (Exception e) {
            logger.warn("TOPIC_DOCKER: Response kafka send outbound message failed.");
        }
    }

    // @KafkaListener(topics = DcimTopicId.TOPIC_SYSTEM_ERROR)
    public void listen14(String message) {
        logger.info("KafkaListener: topics = " + DcimTopicId.TOPIC_SYSTEM_ERROR);
        try {
            send(DcimTopicId.TOPIC_SYSTEM_ERROR, message);
        } catch (Exception e) {
            logger.warn("TOPIC_SYSTEM_ERROR: Response kafka send outbound message failed.");
        }
    }
}
