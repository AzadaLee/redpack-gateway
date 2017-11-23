package com.slfinance.redpack.gateway.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Sean on 2015/8/25.
 */
@Data
@ConfigurationProperties("path")
public class PathProperites {
    private List<String> open;
    private List<String> bypassSignature;
    private Map<Pattern, String> pattern;

    @PostConstruct
    public void init() {
        Set<String> tmp = new HashSet<>();
        // pattern会被重复赋值，在此过滤
        Iterator<Map.Entry<Pattern, String>> it = pattern.entrySet().iterator();
        Map.Entry<Pattern, String> entry;
        String p;
        while (it.hasNext()) {
            entry = it.next();
            p = entry.getKey().pattern();
            if (tmp.contains(p)) {
                // 移除重复值
                it.remove();
                continue;
            }
            tmp.add(p);
        }
    }
}
