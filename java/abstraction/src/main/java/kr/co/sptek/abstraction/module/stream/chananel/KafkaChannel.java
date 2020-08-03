package kr.co.sptek.abstraction.module.stream.chananel;

import kr.co.sptek.abstraction.kafka.stream.KafkaStream;
import kr.co.sptek.abstraction.module.stream.connector.KafkaConnector;
import kr.co.sptek.abstraction.module.stream.connector.KafkaSender;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;
import kr.co.sptek.abstraction.module.stream.proxy.KafkaStreamProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaChannel implements DChannel {

    private static final Logger logger = LogManager.getLogger(KafkaChannel.class);

    String name;
    DStreamProxy inbound;
    DStreamProxy outbound;
    KafkaConnector connector;

    public KafkaChannel(String name) {
        this.name = name;
    }

    public KafkaChannel(KafkaConnector connector, String name) {
        this.name = name;
        this.connector = connector;
    }

    @Override
    public void open() throws Exception {

        // create inboud proxy
        KafkaSender sender = new KafkaSender(connector, this.name + " - KafkaSender");
        KafkaStream instream = new KafkaStream(sender);
        inbound = new KafkaStreamProxy(instream);
        sender.registry(inbound);

        // create outboud proxy
        KafkaStream outstream = new KafkaStream();
        outbound = new KafkaStreamProxy(outstream);

        // channel open
        inbound.open();
        outbound.open();
    }

    public DStreamProxy inbound(){
        return inbound;
    }

    public DStreamProxy outbound(){
        return outbound;
    }

    public KafkaConnector connector(){
        return this.connector;
    }
}

