package com.slfinance.redpack.gateway.filter;

import com.slfinance.redpack.gateway.model.Result;
import com.slfinance.redpack.gateway.model.Token;
import com.slfinance.redpack.gateway.model.UserSession;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sean on 2015/7/17.
 */
@FeignClient("auth")
public interface AuthenticateClient {
    @RequestMapping(method = RequestMethod.POST, value = "/auth/check", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Result<UserSession> check(Token token);
}
