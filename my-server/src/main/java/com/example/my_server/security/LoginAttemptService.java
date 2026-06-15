package com.example.my_server.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService
{
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_MINUTES = 15;

    private record AttemptInfo(int count, LocalDateTime lastAttempt) {}

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String ip)
    {
        AttemptInfo info = attempts.getOrDefault(ip, new AttemptInfo(0, LocalDateTime.now()));
        attempts.put(ip, new AttemptInfo(info.count() + 1, LocalDateTime.now()));
    }

    public void loginSucceeded(String ip)
    {
        attempts.remove(ip);
    }

    public boolean isBlocked(String ip)
    {
        AttemptInfo info = attempts.get(ip);
        if (info == null) return false;

        // 차단 시간이 지났으면 초기화
        if (info.lastAttempt().isBefore(LocalDateTime.now().minusMinutes(BLOCK_MINUTES)))
        {
            attempts.remove(ip);
            return false;
        }

        return info.count() >= MAX_ATTEMPTS;
    }
}
