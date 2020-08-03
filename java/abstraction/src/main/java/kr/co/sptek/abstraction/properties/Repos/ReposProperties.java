package kr.co.sptek.abstraction.properties.Repos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(value = "dcim")
public class ReposProperties {

    private List<RemoteProperties> repository;

    public List<RemoteProperties> getRepository() {
        return repository;
    }

    public void setRepository(List<RemoteProperties> repository) {
        this.repository = repository;
    }



    @Override
    public String toString() {
        return "ReposProperties: [repository :" + repository.toString() + "]";
    }
}
