package kr.co.sptek.abstraction.properties.device;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(value= "mdc")
public class DeviceProperties {
    private List<Device> device;

    public List<Device> getDevice() {
        return device;
    }

    public void setDevice(List<Device> device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "DeviceProperties: [device=" + device.toString() + "]";
    }
}
