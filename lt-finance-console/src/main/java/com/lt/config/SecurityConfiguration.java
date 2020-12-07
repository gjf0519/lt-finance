package com.lt.config;

import com.lt.common.ConsoleConstants;
import com.lt.security.SecurityJdbcUserService;
import com.lt.security.filter.AccessAuthenticationFilter;
import com.lt.security.handler.CustomAccessDeniedHandler;
import com.lt.security.handler.CustomUnauthorizedHandler;
import com.lt.security.provider.MobileAuthenticationProvider;
import com.lt.security.provider.PasswordAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author gaijf
 * @description
 * @date 2020/10/30
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public SecurityJdbcUserService userDetailsService(){
        return new SecurityJdbcUserService();
    }

    @Bean
    public PasswordAuthenticationProvider passwordAuthenticationProvider(){
        return new PasswordAuthenticationProvider(userDetailsService());
    }

    @Bean
    public MobileAuthenticationProvider mobileAuthenticationProvider(){
        return new MobileAuthenticationProvider(userDetailsService());
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomUnauthorizedHandler customunauthorizedHandler(){
        return new CustomUnauthorizedHandler();
    }

    @Bean
    public AccessAuthenticationFilter accessAuthenticationFilter(){
        return new AccessAuthenticationFilter();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder())
                .and()
                .authenticationProvider(mobileAuthenticationProvider())
                .authenticationProvider(passwordAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(ConsoleConstants.ROUTES).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                    .accessDeniedHandler(customAccessDeniedHandler())
                    .authenticationEntryPoint(customunauthorizedHandler())
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterAfter(accessAuthenticationFilter(),LogoutFilter.class);
    }
}
