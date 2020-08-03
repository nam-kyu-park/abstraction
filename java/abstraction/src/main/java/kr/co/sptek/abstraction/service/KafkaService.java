package kr.co.sptek.abstraction.service;

import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;

public class KafkaService implements IService {

    DStreamProxy inbound;
    DStreamProxy outbound;

    @Override
    public DStreamProxy proxy(ProxyRules type) throws Exception {
        return (type == ProxyRules.INBOUND) ? inbound : outbound;
    }

    @Override
    public void setProxy(DStreamProxy proxy, ProxyRules type) throws Exception {
        if (type == ProxyRules.INBOUND) {
            inbound = proxy;
        } else {
            outbound = proxy;
        }
    }

    @Override
    public void sendMessage(String topic, String data) throws Exception {

    }

    @Override
    public String receiveMessage(String topic) throws Exception {
        return null;
    }

    public void registry(IService service) throws Exception  {
    }
}
