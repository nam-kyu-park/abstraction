package kr.co.sptek.abstraction.properties.Repos;

import org.springframework.stereotype.Component;

import java.util.List;

public class ReposEntry {

    List<RemoteProperties> properties;

    public ReposEntry(List<RemoteProperties> properties) {
        this.properties = properties;
    }

    public List<RemoteProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<RemoteProperties> properties) {
        this.properties = properties;
    }

    public int indexOf(String repos) {
        for( RemoteProperties rp : getProperties())
            if (repos.toUpperCase().equals(rp.getName().toUpperCase().trim()))
                return properties.indexOf(rp);
        return -1;
    }

    public String getType(int index) {
        return properties.get(index).getType();
    }

    public String getServer(int index) {
        return properties.get(index).getServer();
    }

    public String getPort(int index) {
        return properties.get(index).getPort();
    }

    public String getUser(int index) {
        return properties.get(index).getUser();
    }

    public String getPassword(int index) {
        return properties.get(index).getPassword();
    }

    public String getRemote(int index) {
        return properties.get(index).getRemote();
    }

}
