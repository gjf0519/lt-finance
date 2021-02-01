package com.lt.security.handler;

import com.alibaba.fastjson.JSON;
import com.lt.common.ConsoleConstants;
import com.lt.web.ResultCode;
import com.lt.web.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gaijf
 * @description 自定义权限不足返回
 * @date 2020/11/18
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.info("权限不足或未登录:{}",accessDeniedException);
        String access_token = request.getHeader(ConsoleConstants.AUTHENTICATION_HEAD);
        if(StringUtils.isEmpty(access_token)){
            response.sendRedirect(request.getContextPath() + "/login.html");
        }else {
            ResultCode resultCode = ResultCode.AUTH_403_3;
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(ResultEntity.fail(resultCode.getCode(),resultCode.getVal())));
        }
    }
}
