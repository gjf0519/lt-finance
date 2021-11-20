package com.lt.security;

import com.lt.web.service.UserService;
import com.lt.view.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author gaijf
 * @description
 * @date 2020/11/5
 */
public class SecurityJdbcUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        LoginUserEntity loginUser = userService.loadUserByUsername(name);
        if(null == loginUser){
            throw new BadCredentialsException(ResultCode.AUTH_401_1.getVal());
        }
        return loginUser;
    }

    public LoginUserEntity loadUserByNameEmail(String name) throws UsernameNotFoundException {
        LoginUserEntity loginUser = userService.loadUserByNameEmail(name);
        if(null == loginUser){
            throw new BadCredentialsException(ResultCode.AUTH_401_1.getVal());
        }
        return loginUser;
    }

    public LoginUserEntity loadUserByMobile(String mobile) throws UsernameNotFoundException {
        LoginUserEntity loginUser = userService.loadUserByMobile(mobile);
        if(null == loginUser){
            throw new BadCredentialsException(ResultCode.AUTH_401_1.getVal());
        }
        return loginUser;
    }
}
