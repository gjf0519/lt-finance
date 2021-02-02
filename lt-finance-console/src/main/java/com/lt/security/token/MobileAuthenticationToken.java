package com.lt.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author gaijf
 * @description 手机验证实体
 * @date 2020/11/5
 */
public class MobileAuthenticationToken extends AbstractAuthenticationToken {
    private String userId;
    private Object principal;

    public MobileAuthenticationToken(){
        super(null);
    }

    public MobileAuthenticationToken(String mobile) {
        super(null);
        this.principal = mobile;
        setAuthenticated(false);
        userId = null;
    }

    public MobileAuthenticationToken(Object principal,String userId,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.userId = userId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
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

    public boolean getAuthenticated() {
        return super.isAuthenticated();
    }

    public void setAuthenticated(boolean authenticated) {
        super.setAuthenticated(authenticated);
    }
}
