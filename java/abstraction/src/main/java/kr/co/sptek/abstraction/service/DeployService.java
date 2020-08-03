package kr.co.sptek.abstraction.service;

 //import kr.co.sptek.abstraction.common.DcimTopicId;
import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.kafka.topic.DeployTopic;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;
import kr.co.sptek.abstraction.service.handler.DeployHandler;
import kr.co.sptek.abstraction.service.handler.DeployHandlerEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import sptek.dcim.common.DcimTopicId;

import java.io.IOException;

@Service
public class DeployService extends KafkaService {

    private static final Logger logger = LogManager.getLogger(DeployService.class);

//    @Autowired
//    DeployHandler handler;

    @Autowired
    DeployHandlerEx handler;

    @Override
    public void setProxy(DStreamProxy proxy, ProxyRules type) throws Exception {
        super.setProxy(proxy, type);
        this.handler.setProxy(proxy, type);
    }

    @KafkaListener(topics = DcimTopicId.TOPIC_DEPLOY)
    public void listen10(String message) {
        logger.info("KafkaListener: topic = " + DcimTopicId.TOPIC_DEPLOY + "data = " + message);
        try {
            DeployTopic topic = DeployTopic.deserializeJSON(message);
            if (topic != null) {
                handler.target(topic);
                handler.useRepository("");
                for (String device : topic.mdc_id) {
                    handler.download(device);
                    handler.activate(device);
                    handler.compress(device);
                    handler.deploy(device);
                }
                handler.flush();
            }
        }

        catch (IOException e) {
            logger.warn("TOPIC_DEPLOY: Response Host handling failed.");
        }

        catch (NullPointerException e) {
            logger.warn("TOPIC_DEPLOY: Response Host handling failed.");
        }
    }
}
