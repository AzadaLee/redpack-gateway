package com.slfinance.redpack.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import com.slfinance.redpack.gateway.model.Result;
import com.slfinance.redpack.gateway.model.Token;
import com.slfinance.redpack.gateway.model.UserSession;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Sean on 2015/7/10.
 */
@Component
public class AuthorizationFilter extends PathFilter {

    public static final String TOKEN_HEADER = "at";
    public static final String ME_HEADER = "me";

    @Autowired
    AuthenticateClient authenticateClient;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 9;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (hasError(ctx)) {
            return false;
        }
        // 公开路径无须检查权限
        return !pathMatch(pathProperites.getOpen(), (String) ctx.get("requestURI"));
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        // 请求路径
        String token = ctx.getRequest().getHeader(TOKEN_HEADER);
        String userId = null;
        if (StringUtils.hasText(token)) {
            Token t = new Token();
            t.setToken(token);
            Result<UserSession> data;
            try {
                data = authenticateClient.check(t);
            } catch (Throwable e) {
                if (!(e instanceof RetryableException) && (e instanceof FeignException)) {
                    // Token 无效
                    setError(ctx, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                } else {
                    // 认证服务不可用
                    setError(ctx, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Authenticate service is not available");
                }
                ctx.setThrowable(e);
                return null;
            }
            UserSession user = data.getResult();
            if (user == null) {
                setError(ctx, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return null;
            }
            String path = (String) ctx.get("requestURI");
            String type = user.getType();
            // 从上到下检查角色路径，只要有一个通过即可
            boolean denied = true;
            Map<Pattern, String> patterns = pathProperites.getPattern();
            for (Pattern pattern : patterns.keySet()) {
                if (pattern.matcher(path).matches() && patterns.get(pattern).equals(type)) {
                    denied = false;
                    break;
                }
            }
            if (denied) {
                setError(ctx, HttpServletResponse.SC_FORBIDDEN, "Permission denied");
                return null;
            }
            userId = String.valueOf(user.getId());
        }
        if (userId != null) {
            // 保存当前用户ID
            ctx.addZuulRequestHeader(ME_HEADER, userId);
        } else {
            // 结束后续处理并返回401错误
            setError(ctx, HttpServletResponse.SC_UNAUTHORIZED, "Permission denied");
        }
        return null;
    }

}
