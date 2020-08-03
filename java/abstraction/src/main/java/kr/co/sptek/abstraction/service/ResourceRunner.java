package kr.co.sptek.abstraction.service;

import kr.co.sptek.abstraction.properties.ResourceProperties;
import kr.co.sptek.abstraction.util.PathUtil;
import org.springframework.stereotype.Component;

@Component
public class ResourceRunner {
    public static void load(ResourceProperties reposProperties) {
        PathUtil.mkdirs(reposProperties.absolutePath(""));
    }
}

