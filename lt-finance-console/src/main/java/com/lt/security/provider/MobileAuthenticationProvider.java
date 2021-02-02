package com.lt.security.provider;

import com.lt.security.LoginUserEntity;
import com.lt.security.SecurityJdbcUserService;
import com.lt.security.token.MobileAuthenticationToken;
import com.lt.view.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author gaijf
 * @description 手机认证管理
 * @date 2020/11/5
 */
@Slf4j
public class MobileAuthenticationProvider extends OpenAuthenticationProvider {

    public MobileAuthenticationProvider(SecurityJdbcUserService securityJdbcUserService) {
        super(securityJdbcUserService);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobileAuthenticationToken token = (MobileAuthenticationToken) authentication;
        String mobile = (String) token.getPrincipal();
        LoginUserEntity loginUser = securityJdbcUserService.loadUserByMobile(mobile);
        if(null == loginUser){
            throw new BadCredentialsException(ResultCode.AUTH_401_1.getVal());
        }else if (loginUser.isEnabled()) {
            throw new DisabledException(ResultCode.AUTH_403_2.getVal());
        }
        MobileAuthenticationToken authenticationResult = new
                MobileAuthenticationToken(loginUser.getPhone(),loginUser.getId(),loginUser.getAuthorities());
        return authenticationResult;
    }
}
