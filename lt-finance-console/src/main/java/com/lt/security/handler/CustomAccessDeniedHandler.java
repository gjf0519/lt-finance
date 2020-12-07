package com.lt.security.handler;

import com.alibaba.fastjson.JSON;
import com.lt.web.ResultCode;
import com.lt.web.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

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
        log.info("未开通权限:{}",accessDeniedException);
        ResultCode resultCode = ResultCode.AUTH_403_1;
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(ResultEntity.fail(resultCode.getCode(),resultCode.getVal())));
    }
}
