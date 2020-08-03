package kr.co.sptek.abstraction.properties.Repos;

import org.springframework.stereotype.Component;

public class RemoteProperties {

    private String name;
    private String type;
    private String server;
    private String port;
    private String user;
    private String password;
    private String remote;
    private String includeEntry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getIncludeEntry() {
        return includeEntry;
    }

    public void setIncludeEntry(String includeEntry) {
        this.includeEntry = includeEntry;
    }

    @Override
    public String toString() {
        return "RemoteProperties: ["
                + "name=" + name
                + ", type=" + type
                + ", server=" + server
                + ", port=" + port
                + ", user=" + user
                + ", password=" + password
                + ", remote=" + remote
                + ", includeEntry=" + includeEntry + "]";
    }
}
