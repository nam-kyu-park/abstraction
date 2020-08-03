package kr.co.sptek.abstraction.properties.device;

public class Device {

    private String name;
    private String host;
    private String type;
    private String control;
    private String display;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "Device: " +
                "[name=" + name +
                ", host=" + host +
                ", type=" + type +
                ", control=" + control +
                ", display=" + display +
                "]";
    }
}
