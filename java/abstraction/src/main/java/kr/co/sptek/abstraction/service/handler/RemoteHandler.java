package kr.co.sptek.abstraction.service.handler;

import kr.co.sptek.abstraction.service.KafkaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RemoteHandler extends KafkaService {

    private static final Logger logger = LogManager.getLogger(RemoteHandler.class);

    public void execute(String message) {

    }
}
