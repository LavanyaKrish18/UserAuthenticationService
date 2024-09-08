package org.example.userauthenticationservice.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@Configuration
public class SpringSecurity {

    /*
    try - http://localhost:8080/login
    default login feature provided by spring security
    also it doesn't allow httpSecurity requests.
    hence we have to disable default behavior and allow all httpSecurity requests
     */
    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().disable();
        httpSecurity.csrf().disable();
        httpSecurity.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey() {
        MacAlgorithm algorithm = Jwts.SIG.HS256;
        return algorithm.key().build();
    }
}
