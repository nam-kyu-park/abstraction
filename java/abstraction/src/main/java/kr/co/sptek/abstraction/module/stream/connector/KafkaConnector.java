package kr.co.sptek.abstraction.module.stream.connector;

import kr.co.sptek.abstraction.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

public class KafkaConnector implements DConnector{

   @Autowired
   KafkaTemplate<String, String> kafkaTemplate;

    List<IService>  serviceList;

    public KafkaConnector(){
        serviceList = new ArrayList<IService>();
    }

    public void addListener(IService service) {
        serviceList.add(service);
    }

    public String toString() {
        return "KafkaConnector[ None Data ]";
    }

    public KafkaTemplate<String, String> driver(){
        return kafkaTemplate;
    }
}
