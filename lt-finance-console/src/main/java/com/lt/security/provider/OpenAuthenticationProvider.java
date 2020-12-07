package com.lt.security.provider;

import com.lt.security.SecurityJdbcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author gaijf
 * @description
 * @date 2020/11/11
 */
public abstract class OpenAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    protected PasswordEncoder passwordEncoder;
    protected SecurityJdbcUserService securityJdbcUserService;

    public OpenAuthenticationProvider(SecurityJdbcUserService securityJdbcUserService){
        this.securityJdbcUserService = securityJdbcUserService;
    }
}
