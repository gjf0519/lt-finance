package com.lt.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author gaijf
 * @description 用户名密码验证实体
 * @date 2020/11/5
 */
public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    private String userId;
    private Object principal;
    private Object credentials;

    public PasswordAuthenticationToken(){
        super(null);
    }

    public PasswordAuthenticationToken(String mobile, String credentials) {
        super(null);
        this.userId = null;
        this.principal = mobile;
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    public PasswordAuthenticationToken(Object principal, Object credentials, String userId,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.userId = userId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public boolean getAuthenticated() {
        return super.isAuthenticated();
    }

    public void setAuthenticated(boolean authenticated) {
        super.setAuthenticated(authenticated);
    }

}
