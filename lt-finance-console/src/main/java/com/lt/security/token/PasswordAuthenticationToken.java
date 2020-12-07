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

    private final String userId;
    private final Object principal;
    private final Object credentials;

    public PasswordAuthenticationToken(String mobile, String credentials) {
        super(null);
        this.userId = null;
        this.principal = mobile;
        this.credentials = credentials;
        setAuthenticated(false);
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

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
