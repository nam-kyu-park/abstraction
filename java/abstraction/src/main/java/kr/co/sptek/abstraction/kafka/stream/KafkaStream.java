package kr.co.sptek.abstraction.kafka.stream;

import kr.co.sptek.abstraction.module.stream.DPair;
import kr.co.sptek.abstraction.module.stream.DStreamIO;
import kr.co.sptek.abstraction.module.stream.connector.KafkaSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaStream extends DStreamIO {

    private static final Logger logger = LogManager.getLogger(KafkaStream.class);

    DPair data;
    KafkaSender sender;

    public KafkaStream() {
    }

    public KafkaStream(KafkaSender sender) {
        this.sender = sender;
    }

    public String toString() {
        return "KafkaStream[ data: " + data.toString() + ", " + sender.toString() + " ]";
    }

    @Override
    public void open() throws Exception {
        super.open();
        if(this.sender != null) {
            this.sender.start();
        }
    }

    @Override
    public Object poll() throws Exception  {
        Object item = super.poll();
        this.data = (DPair)item;
        return item;
    }

    public void send(String topic, String data) throws Exception {
        send(new DPair<String, String>(topic, data));
    }

    static public String topic(DPair data) throws Exception {
        return (String)data.getKey();
    }

    static public String topic(Object data) throws Exception {
        return topic((DPair)data);
    }

    public String topic() throws Exception {
        return topic(this.data);
    }

    static public String data(DPair data) throws Exception {
        return (String)data.getValue();
    }

    static public String data(Object data) throws Exception {
        return data((DPair)data);
    }

    public String data() throws Exception {
        return data(this.data);
    }
}
