package com.lt.security.provider;

import com.lt.security.LoginUserEntity;
import com.lt.security.SecurityJdbcUserService;
import com.lt.security.token.PasswordAuthenticationToken;
import com.lt.view.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author gaijf
 * @description
 * @date 2020/11/5
 */
@Slf4j
public class PasswordAuthenticationProvider extends OpenAuthenticationProvider {

    public PasswordAuthenticationProvider(SecurityJdbcUserService securityJdbcUserService) {
        super(securityJdbcUserService);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        LoginUserEntity loginUser = securityJdbcUserService.loadUserByNameEmail(username);
        if(null == loginUser){
            throw new BadCredentialsException(ResultCode.AUTH_401_1.getVal());
        }else if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            throw new BadCredentialsException(ResultCode.AUTH_401_2.getVal());
        }else if (loginUser.isEnabled()) {
            throw new DisabledException(ResultCode.AUTH_403_2.getVal());
        }
        PasswordAuthenticationToken authenticationResult = new
                PasswordAuthenticationToken(username,password,loginUser.getId(), loginUser.getAuthorities());
        return authenticationResult;
    }
}
