package com.slfinance.redpack.gateway.filter;

import com.slfinance.redpack.gateway.model.PathProperites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import java.util.List;

/**
 * Created by Sean on 2016/11/16.
 */
public abstract class PathFilter extends BaseFilter {

    @Autowired
    protected PathProperites pathProperites;

    private PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 检查路径是否在列表范围内
     */
    protected boolean pathMatch(List<String> patterns, String path) {
        if (path == null) {
            return false;
        }
        Assert.notEmpty(patterns, "Patterns is empty, can not eval " + path);
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

}
