package com.example.my_server.config;

import com.example.my_server.domain.Role;
import com.example.my_server.domain.User;
import com.example.my_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminAccountInitializer implements CommandLineRunner
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception
    {
        if (!userRepository.existsByLoginId("admin"))
        {
            User admin = new User();
            admin.setLoginId("admin");
            admin.setUsername("관리자");
            admin.setPassword(bCryptPasswordEncoder.encode("admin1019"));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("✅ 관리자 계정(admin)이 생성되었습니다.");
        }
    }
}
