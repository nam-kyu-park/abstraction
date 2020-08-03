package kr.co.sptek.abstraction.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value= "spring.kafka")
public class KafkaProperties {
	String consumerGroupId;
	String bootstrapServers;
	public String getConsumerGroupId() {
		return consumerGroupId;
	}
	public void setConsumerGroupId(String consumerGroupId) {
		this.consumerGroupId = consumerGroupId;
	}
	public String getBootstrapServers() {
		return bootstrapServers;
	}
	public void setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}
	
	@Override
	public String toString() {
		return "KafkaProperties: [consumerGroupId=" + consumerGroupId + ", bootstrapServers=" + bootstrapServers + "]";
	}

	
}
