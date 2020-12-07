package com.lt.security.handler;

import com.alibaba.fastjson.JSON;
import com.lt.web.ResultCode;
import com.lt.web.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author gaijf
 * @description 自定义错误异常返回
 * @date 2020/11/5
 */
@Slf4j
public class CustomUnauthorizedHandler implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        log.info("认证异常:{}",authenticationException);
        ResultCode resultCode = getErrorMsg(authenticationException);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultEntity.fail(resultCode.getCode(),resultCode.getVal())));
    }

    private ResultCode getErrorMsg(AuthenticationException e){
        Optional<ResultCode> optional =
                this.getAuthenticationError(e);
        if (optional.isPresent()){
            return optional.get();
        }
        optional = getAuthorizationError(e);
        if (optional.isPresent()){
            return optional.get();
        }
        return ResultCode.FAIL;
    }

    private Optional<ResultCode> getAuthenticationError(AuthenticationException e){
        if (!(e instanceof BadCredentialsException)){
            return Optional.empty();
        }
        if(ResultCode.AUTH_401_1.getVal()
                .equals(e.getMessage())){
            return Optional.of(ResultCode.AUTH_401_1);
        }else if(ResultCode.AUTH_401_2.getVal()
                .equals(e.getMessage())){
            return Optional.of(ResultCode.AUTH_401_2);
        }
        return Optional.empty();
    }

    private Optional<ResultCode> getAuthorizationError(AuthenticationException e){
        if(e instanceof DisabledException){
            return Optional.of(ResultCode.AUTH_403_2);
        }
        if(e instanceof InsufficientAuthenticationException) {
            return Optional.of(ResultCode.AUTH_403_1);
        }
        return Optional.empty();
    }
}
