package kr.co.sptek.abstraction.properties.device;

import kr.co.sptek.abstraction.properties.Repos.RemoteProperties;

import java.util.List;

public class DeviceEntry {

    private List<Device> properties;

    public DeviceEntry(List<Device> properties) {
        this.properties = properties;
    }

    public List<Device> getProperties() {
        return properties;
    }

    public void setProperties(List<Device> properties) {
        this.properties = properties;
    }

    public int indexOf(String name) {
        for( Device device : getProperties())
            if (device.getName().toLowerCase().equals(name.toLowerCase()))
                return properties.indexOf(device);
        return -1;
    }

    public Device getDevice(int index) {
        return properties.get(index);
    }

    public String getName(int index) {
        return properties.get(index).getName();
    }

    public String getHost(int index) {
        return properties.get(index).getHost();
    }

    public String getType(int index) {
        return properties.get(index).getType();
    }

    public String getControl(int index) {
        return properties.get(index).getControl();
    }

    public String getDisplay(int index) {
        return properties.get(index).getDisplay();
    }

    @Override
    public String toString() {
        return "DeviceEntry: [properties=" + properties.toString() + "]";
    }
}
