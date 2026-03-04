package com.example.my_server.service;

import com.example.my_server.domain.Role;
import com.example.my_server.domain.User;
import com.example.my_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(User user)
    {
        String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public boolean checkLoginIdDuplicate(String loginId)
    { return userRepository.existsByLoginId(loginId); }

    public boolean checkUsernameDuplicate(String username)
    { return userRepository.existsByUsername(username); }
}
