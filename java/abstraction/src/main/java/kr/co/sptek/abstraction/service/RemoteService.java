package kr.co.sptek.abstraction.service;

import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;
import kr.co.sptek.abstraction.service.handler.RemoteHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import sptek.dcim.common.DcimTopicId;

public class RemoteService extends KafkaService {

    private static final Logger logger = LogManager.getLogger(RemoteService.class);

    @Autowired
    RemoteHandler handler;

    @Override
    public void setProxy(DStreamProxy proxy, ProxyRules type) throws Exception {
        super.setProxy(proxy, type);
        this.handler.setProxy(proxy, type);
    }

    @KafkaListener(topics = DcimTopicId.TOPIC_REMOTE)
    public void listen10(String message) {
        logger.info("KafkaListener: topic = " + DcimTopicId.TOPIC_REMOTE + "data = " + message);
        try {
            handler.execute(message);
        }
        catch (NullPointerException e) {
            logger.warn("TOPIC_REMOTE: Response remote handling failed.");
        }
    }
}
