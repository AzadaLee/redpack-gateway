package com.slfinance.redpack.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Sean on 2015/8/20.
 */
public abstract class BaseFilter extends ZuulFilter {

    protected static final Log LOG = LogFactory.getLog("RedPackGateway");

    protected static final String STATUS_CODE = "error.status_code";

    protected void setError(RequestContext ctx, int code, String message) {
        ctx.setSendZuulResponse(false);
        ctx.getRequest().setAttribute("javax.servlet.error.message", message);
        ctx.set(STATUS_CODE, code);
        LOG.warn("Error: [" + message + "] (Code: " + code + ")");
    }

    protected boolean hasError(RequestContext ctx) {
        return ctx.get(STATUS_CODE) != null;
    }

}
