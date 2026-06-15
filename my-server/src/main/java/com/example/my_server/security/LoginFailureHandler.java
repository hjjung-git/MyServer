package com.example.my_server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
    private final LoginAttemptService loginAttemptService;

    public LoginFailureHandler(LoginAttemptService loginAttemptService)
    {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException
    {
        String ip = request.getRemoteAddr();
        loginAttemptService.loginFailed(ip);

        String redirectUrl = loginAttemptService.isBlocked(ip)
                ? "/user/login?error=blocked"
                : "/user/login?error=true";

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
