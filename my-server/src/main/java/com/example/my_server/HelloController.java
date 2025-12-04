package com.example.my_server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 클래스는 웹 요청을 처리하는 컨트롤러입니다.
@RestController
public class HelloController {

    // '/hello' 라는 경로로 GET 요청이 오면, 이 메소드가 실행됩니다.
    @GetMapping("/hello")
    public String sayHello() {
        return "안녕하세요! 나의 첫 Spring Boot 서버입니다!";
    }
}