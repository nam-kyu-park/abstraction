package kr.co.sptek.abstraction.service.request;

import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.module.stream.proxy.KafkaStreamProxy;
import kr.co.sptek.abstraction.service.IService;
import kr.co.sptek.abstraction.service.KafkaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value= "KafkaRequest")
public class KafkaRequest extends KafkaService {

    private static final Logger logger = LogManager.getLogger(KafkaRequest.class);

    @Override
    public void registry(IService service) throws Exception  {
        service.setProxy(this.proxy(ProxyRules.INBOUND), ProxyRules.INBOUND);
    }

    @Override
    public void sendMessage(String topic, String data) throws Exception
    {
        if(topic == null || topic.isEmpty())
            throw new Exception();

        if(data == null || data.isEmpty())
            throw new Exception();

        KafkaStreamProxy proxy = (KafkaStreamProxy)this.proxy(ProxyRules.INBOUND);
        proxy.send(topic, data);
    }
}
