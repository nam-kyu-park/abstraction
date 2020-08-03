package kr.co.sptek.abstraction.service;

import kr.co.sptek.abstraction.common.ProxyRules;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;

public interface IService {
    DStreamProxy proxy(ProxyRules type) throws Exception;
    void setProxy(DStreamProxy proxy, ProxyRules type) throws Exception;
    void sendMessage(String topic, String data) throws Exception;
    String receiveMessage(String topic) throws Exception;
}
