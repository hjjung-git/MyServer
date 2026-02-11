package com.example.my_server.exception;

// RuntimeException을 상속받아 별도의 throws 선언 없이 사용 가능
public class PostNotFoundException extends RuntimeException
{
    public PostNotFoundException(String message) { super(message); }
}