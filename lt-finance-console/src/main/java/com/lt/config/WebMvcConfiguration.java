package com.lt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2.0以前WebMvcConfigurerAdapter
 * 2.0以后WebMvcConfigurer
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //在security执行之后执行
        registry.addViewController("/").setViewName("redirect:login.html");
    }

    /**
     * 添加静态资源，过滤swagger-api
     *默认静态资源路径，优先级顺序为：META-INF/resources > resources > static > public
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
