package com.lt.service;

import com.lt.mapper.UserMapper;
import com.lt.security.LoginUserEntity;
import com.lt.security.token.PasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author gaijf
 * @description
 * @date 2020/12/2
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginUserEntity loadUserByUsername(String username) throws AuthenticationException {
        LoginUserEntity loginUser = userMapper.loadUserByUsername(username);
        if(null != loginUser){
            return loginUser;
        }
        //查询用户中心数据
        //查询不为空保存用户信息到本地
        return loginUser;
    }

    public LoginUserEntity loadUserByNameEmail(String username) {
        LoginUserEntity loginUser = userMapper.loadUserByNameEmail(username);
        if(null != loginUser){
            return loginUser;
        }
        //查询用户中心数据
        //查询不为空保存用户信息到本地
        return loginUser;
    }

    public LoginUserEntity loadUserByMobile(String mobile) {
        LoginUserEntity loginUser = userMapper.loadUserByMobile(mobile);
        if(null != loginUser){
            return loginUser;
        }
        //查询用户中心数据
        //查询不为空保存用户信息到本地
        return loginUser;
    }

    public String getUserTokenInfo(String username, String password) {
        PasswordAuthenticationToken token = new PasswordAuthenticationToken(username, password);
        PasswordAuthenticationToken authentication =
                (PasswordAuthenticationToken) authenticationManager.authenticate(token);
        String userId = authentication.getUserId();
        String accessToken = tokenService.createAccessToken(authentication);
        return accessToken;
    }
}
