package com.lt.service;

import com.lt.utils.GenerateUUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author gaijf
 * @description 访问令牌管理
 * @date 2020/11/6
 */
@Slf4j
@Service
public class TokenService {

    private static final int timeout = 60 * 60 * 2;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public String createAccessToken(Authentication authentication){
        String accessToken = GenerateUUID.getUUID();
        redisTemplate.opsForValue().set(accessToken, authentication, timeout, TimeUnit.SECONDS);
        return accessToken;
    }
}
