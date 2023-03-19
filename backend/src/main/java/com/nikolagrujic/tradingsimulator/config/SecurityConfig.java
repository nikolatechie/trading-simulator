package com.nikolagrujic.tradingsimulator.config;

import com.nikolagrujic.tradingsimulator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SuppressWarnings("deprecation")
@EnableWebSecurity
@AllArgsConstructor
@Import(PasswordEncoderConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/user/register", "/api/user/verify").permitAll()
            .anyRequest().authenticated() // Require authentication for all other requests
            .and()
            .csrf().disable() // Disable CSRF protection for simplicity
            .formLogin(); // Enable form-based authentication
    }
}
