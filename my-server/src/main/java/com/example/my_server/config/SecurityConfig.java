package com.example.my_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    // 비밀번호 암호화 객체
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/main/list/**", "/user/**", "/uploads/**", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/loginProc")
                        .usernameParameter("loginId")
                        .defaultSuccessUrl("/main/list")
                        .permitAll()
                )

                .logout((logout) -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/main/list")
                        .permitAll()
                )

                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                .csrf((csrf) -> csrf.disable());

        return http.build();
    }
}
