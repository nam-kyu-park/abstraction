package kr.co.sptek.abstraction.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value= "test")
public class TestReposProperties {

    String jsonRoot;
    String workspace;

    public String getJsonRoot() {
        return jsonRoot;
    }

    public void setJsonRoot(String jsonRoot) {
        this.jsonRoot = jsonRoot;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Override
    public String toString() {
        return "TestReposProperties: [json-root=" + jsonRoot + ", workspace=" + workspace + "]";
    }
}
