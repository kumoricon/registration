package org.kumoricon.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // No authorization required for static resources, images, etc
                .antMatchers("/css/**", "/js/**", "/img/**", "/healthcheck", "/favicon.ico").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .sessionManagement()
            .and()
                .logout().deleteCookies("JSESSIONID")
            .and()
                .formLogin();

        http.headers().contentSecurityPolicy("script-src 'self'");

    }
}