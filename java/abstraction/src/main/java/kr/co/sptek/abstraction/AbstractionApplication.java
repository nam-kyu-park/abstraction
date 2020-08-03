package kr.co.sptek.abstraction;

import kr.co.sptek.abstraction.service.ServiceRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AbstractionApplication {

    public static void main(String[] args) {
//        ApplicationContext applicationContext = SpringApplication.run(AbstractionApplication.class, args);

//        ConfigurableApplicationContext context = SpringApplication.run(AbstractionApplication.class, args);
//       context.start();

        ConfigurableApplicationContext context = SpringApplication.run(AbstractionApplication.class, args);
//        ServiceRunner service = applicationContext.getBean(ServiceRunner.class);
//        service.run();
    }

}
