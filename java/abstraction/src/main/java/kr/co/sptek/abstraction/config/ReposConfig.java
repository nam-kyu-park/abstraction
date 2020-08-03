package kr.co.sptek.abstraction.config;

import kr.co.sptek.abstraction.properties.Repos.ReposEntry;
import kr.co.sptek.abstraction.properties.Repos.ReposProperties;
import kr.co.sptek.abstraction.properties.ResourceProperties;
import kr.co.sptek.abstraction.repos.*;
import kr.co.sptek.abstraction.service.ResourceRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReposConfig {

    private static final Logger logger = LogManager.getLogger(ReposConfig.class);

    @Autowired
    ResourceProperties resourceProperties;

    @Autowired
    ReposProperties reposProp;

    ReposConnector connectors;

    public boolean exists() {
        return (connectors != null);
    }

    @Bean
    public ReposConnector reposConnector() {
        ResourceRunner.load(resourceProperties);
        ReposEntry entry = new ReposEntry(reposProp.getRepository());
        ReposConnector connector = new ReposConnector(resourceProperties);
        connector.create(entry);
        return connector;
    }

    @Bean
    public ReposUploader reposUploader(){
        if( !exists() )
            connectors = reposConnector();
        return connectors;
    }

    @Bean
    public ReposDownloader reposDownloader(){
        if( !exists() )
            connectors = reposConnector();
        return connectors;
    }

    @Bean
    public ReposAttribute reposAttribute(){
        if( !exists() )
            connectors = reposConnector();
        return connectors;
    }

    @Bean
    public DeploymentExecutor deploymentExecutor(){
        if( !exists() )
            connectors = reposConnector();
        return connectors;
    }
}
