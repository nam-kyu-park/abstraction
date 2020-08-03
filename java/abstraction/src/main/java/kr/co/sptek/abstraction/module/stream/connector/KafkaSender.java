package kr.co.sptek.abstraction.module.stream.connector;

import kr.co.sptek.abstraction.kafka.stream.KafkaStream;
import kr.co.sptek.abstraction.module.stream.proxy.DStreamProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class KafkaSender extends Thread{

    private static final Logger logger = LogManager.getLogger(KafkaSender.class);

    public KafkaConnector connector;
    public DStreamProxy proxy;

    public KafkaSender(KafkaConnector connector, String name){
        super(name);
        this.connector = connector;
    }

    public String toString() {
        return "KafkaSender [ stream-connector" + connector.toString() + "]";
    }

    public void registry(DStreamProxy proxy) {
        this.proxy = proxy;
    }

    public void run(){
        logger.info(this.getName() + " running.");
        try {
            while (true) {
                if (!proxy.isEmpty()) {
                    Object data = proxy.poll();
                    String topic = KafkaStream.topic(data);
                    String msg = KafkaStream.data(data);
                    send(topic, msg);
                }
                Thread.yield();
            }
        }

        catch (Exception e)
        {
            logger.warn(this.getName() + "send topic failed.");
        }
    }

    public void send(String topic, String msg) {
        KafkaTemplate<String, String> kafkaTemplate = connector.driver();
        if (kafkaTemplate == null) {
            logger.info("kafkaTemplate is null instance");
        }
        else {
            // logger.info("call send: topic: " + topic + ", data: " + msg);
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, msg);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    // TODO kafka fail message
                    logger.info("kafka fail msg - topic : " + topic + " / msg : " + msg + " / result : " + ex.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    // TODO kafka sucess message
                    logger.info("kafka success msg - topic : " + topic + " / msg : " + msg + " / result : " + result.getRecordMetadata().offset());
                }
            });
        }
    }
}