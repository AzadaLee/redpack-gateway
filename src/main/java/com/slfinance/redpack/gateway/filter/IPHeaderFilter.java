package com.slfinance.redpack.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Sean on 2016/4/19.
 */
@Component
public class IPHeaderFilter extends BaseFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        ctx.addZuulRequestHeader("ip", getRemoteAddr(req));
        return null;
    }

    /**
     * 获取客户端真实IP地址
     * 注：为防止头信息伪造，不解析X-Forwarded-For头
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

}
