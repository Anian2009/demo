package com.example.demo.security;

import com.example.demo.domain.RoleType;
import org.pac4j.core.config.Config;
import org.pac4j.springframework.web.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "org.pac4j.springframework.web")
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private Config config;

    @Autowired
    private Config configNamePassword;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor(config, "HeaderClient", RoleType.USER.toString())).addPathPatterns("/api/user/*");
        registry.addInterceptor(new SecurityInterceptor(config, "HeaderClient", RoleType.ADMIN.toString())).addPathPatterns("/api/admin/*");
        //registry.addInterceptor(new SecurityInterceptor(configNamePassword, "DirectBasicAuthClient", RoleType.ADMIN.toString())).addPathPatterns("/api/guest/log-in");
    }
}
