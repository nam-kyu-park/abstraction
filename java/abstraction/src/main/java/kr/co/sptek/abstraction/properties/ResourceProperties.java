package kr.co.sptek.abstraction.properties;

import kr.co.sptek.abstraction.util.PathUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(value= "dcim.resource")
public class ResourceProperties {

    private static final String SOURCE_PATH = "source";
    private static final String DEPLOY_PATH = "deploy";
    private static final String ACTIVATE_CODE_PATH = "activate-code";
    private static final String HOST_ENTRY_PATH = "host-entry";

    private String collector;
    private String agent;
    private String hosts;
    private String temporary;
    private String adaptation;
    private String release;


    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getTemporary() {
        return temporary;
    }

    public void setTemporary(String temporary) {
        this.temporary = temporary;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getAdaptation() {
        return adaptation;
    }

    public void setAdaptation(String adaptation) {
        this.adaptation = adaptation;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String agentName() {
        return new File(getAgent()).getName();
    }

    public String collectorName() {
        return new File(getCollector()).getName();
    }

    public String adaptationName() {
        return new File(getAdaptation()).getName();
    }

    public String joinPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get("", path).toString()
        );
    }

    public String collectorPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absolutePath(path), collectorName()).toString()
        );
    }

    public String agentPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absolutePath(path), agentName()).toString()
        );
    }

    public String adaptationPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absolutePath(path), agentName()).toString()
        );
    }

    public String activePath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absolutePath(path), ACTIVATE_CODE_PATH).toString()
        );
    }

    public String hostPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absolutePath(path), HOST_ENTRY_PATH).toString()
        );
    }

    public String deployPath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(DEPLOY_PATH, path).toString()
        );
    }

    public String sourcePath(String...path) {
        return FilenameUtils.separatorsToUnix(
                Paths.get(SOURCE_PATH, path).toString()
        );
    }

    public String absolutePath(String...path) {
        if( path.length < 1)
            return absoluteTemporaryPath();

        if(path[0].isEmpty())
            return absoluteTemporaryPath();

        try {
            Path pathBase = Paths.get(absoluteTemporaryPath());
            Path pathAbsolute = Paths.get("", path);
            Path pathRelative = pathBase.relativize(pathAbsolute);
            return FilenameUtils.separatorsToUnix(
                    Paths.get(absoluteTemporaryPath(), pathRelative.toString()).toString()
            );
        }
        catch (IllegalArgumentException e) { }

        return FilenameUtils.separatorsToUnix(
                Paths.get(absoluteTemporaryPath(), path).toString()
        );

    }

    public String absoluteTemporaryPath() {
        return FilenameUtils.separatorsToUnix(
                Paths.get(absoluteExecutePath(), getTemporary()).toString()
        );
    }

    public String absoluteExecutePath() {
        return System.getProperty("user.dir");
    }
}
