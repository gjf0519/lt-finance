package com.lt.security.filter;

import com.lt.common.ViewConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gaijf
 * @description
 * @date 2020/11/6
 */
@Slf4j
public class AccessAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        System.out.println(request.getRequestURI()+"======================");
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            chain.doFilter(request, response);
            return;
        }
        String access_token = request.getHeader(ViewConstants.AUTHENTICATION_HEAD);
        if (StringUtils.isNotEmpty(access_token)) {
            if(!redisTemplate.hasKey(access_token) ||
                    null == redisTemplate.opsForValue().get(access_token)){
                chain.doFilter(request, response);
                return;
            }
            Authentication authentication = (Authentication) redisTemplate.opsForValue().get(access_token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
