package kr.co.sptek.abstraction.module.stream.proxy;

import kr.co.sptek.abstraction.module.stream.DPair;
import kr.co.sptek.abstraction.kafka.stream.KafkaStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaStreamProxy extends DStreamProxy {

    private static final Logger logger = LogManager.getLogger(KafkaStreamProxy.class);

    public KafkaStreamProxy(KafkaStream buffer) {
        super(buffer);
    }

    public String toString() {
        return "KafkaStreamProxy[ " + buffer().toString() + " ]";
    }

    public void send(String topic, String data)  throws Exception {
        send(new DPair<String, String>(topic, data));
    }

}
