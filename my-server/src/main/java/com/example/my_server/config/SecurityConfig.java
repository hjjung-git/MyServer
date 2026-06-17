package com.example.my_server.config;

import com.example.my_server.security.LoginFailureHandler;
import com.example.my_server.security.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final LoginFailureHandler loginFailureHandler;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginFailureHandler loginFailureHandler,
                          LoginSuccessHandler loginSuccessHandler)
    {
        this.loginFailureHandler = loginFailureHandler;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                // 접근 권한
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/main/list/**", "/panel/**", "/user/**", "/uploads/**", "/h2-console/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/post/delete/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )

                // 로그인
                .formLogin((form) -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/loginProc")
                        .usernameParameter("loginId")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )

                // 로그아웃
                .logout((logout) -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/main/list")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // CSRF : H2 콘솔 경로만 제외하고 활성화
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )

                // 보안 헤더
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())         // H2 콘솔 iframe 허용 (same origin만)
                        .contentTypeOptions(ct -> {})                      // X-Content-Type-Options: nosniff
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        )
                );

        return http.build();
    }
}
