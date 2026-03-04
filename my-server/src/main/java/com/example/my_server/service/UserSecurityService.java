package com.example.my_server.service;

import com.example.my_server.domain.User;
import com.example.my_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSecurityService implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException
    {
        System.out.println("### [DEBUG] 검색할 로그인 아이디: " + loginId);

        Optional<User> optional = userRepository.findByLoginId(loginId);

        if (optional.isEmpty())
        {
            System.out.println("### [DEBUG] DB에서 사용자를 찾지 못했습니다.");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId);
        }

        User user = optional.get();
        System.out.println("### [DEBUG] 사용자 찾음: " + user.getLoginId() + ", 비밀번호(해시): " + user.getPassword());

        return user;
    }
}
