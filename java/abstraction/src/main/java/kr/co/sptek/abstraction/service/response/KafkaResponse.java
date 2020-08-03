package kr.co.sptek.abstraction.service.response;

import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.module.stream.proxy.KafkaStreamProxy;
import kr.co.sptek.abstraction.service.IService;
import kr.co.sptek.abstraction.service.KafkaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value= "KafkaResponse")
public class KafkaResponse  extends KafkaService {

    private static final Logger logger = LogManager.getLogger(KafkaResponse.class);

    @Override
    public void registry(IService service) throws Exception  {
        service.setProxy(this.proxy(ProxyRules.OUTBOUND), ProxyRules.OUTBOUND);
    }

    @Override
    public void sendMessage(String topic, String data) throws Exception {
        logger.info("call sendMessage");
    }

    @Override
    public String receiveMessage(String topic) throws Exception {
        logger.info("call recvMessage");
        KafkaStreamProxy proxy = (KafkaStreamProxy)this.proxy(ProxyRules.OUTBOUND);
        Object data = proxy.recv();
        return data.toString();
    }

}
