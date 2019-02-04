package com.example.demo.security;

import com.example.demo.domain.RoleType;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.auth.NamePasswordAuthenticator;
import com.example.demo.security.auth.TokenAuthenticator;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.HeaderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Pac4jConfig {
    public static final String HEADER_TOKEN_NAME = "token";

    private final UsersRepository usersRepository;

    @Autowired
    public Pac4jConfig(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Bean
    public Config config() {
        HeaderClient headerClient = new HeaderClient(HEADER_TOKEN_NAME, new TokenAuthenticator(usersRepository));

        Config config = new Config(headerClient);
        config.addAuthorizer(RoleType.ADMIN.toString(), new RequireAnyRoleAuthorizer(RoleType.ADMIN.toString()));
//        config.addAuthorizer(RoleType.ADMIN.toString(), new RequireAnyRoleAuthorizer(RoleType.USER.toString()));
        config.addAuthorizer(RoleType.USER.toString(), new RequireAnyRoleAuthorizer(RoleType.USER.toString()));
        return config;
    }

    @Bean
    public Config configNamePassword() {
        DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(new NamePasswordAuthenticator(usersRepository));

        Config config = new Config(directBasicAuthClient);
        config.addAuthorizer(RoleType.USER.toString(), new RequireAnyRoleAuthorizer(RoleType.USER.toString()));
//        config.addAuthorizer(RoleType.ADMIN.toString(), new RequireAnyRoleAuthorizer(RoleType.ADMIN.toString(),RoleType.USER.toString()));
//        config.addAuthorizer(RoleType.ADMIN.toString(), new RequireAnyRoleAuthorizer(RoleType.USER.toString()));
        return config;
    }
}
