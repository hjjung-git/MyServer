package com.example.my_server.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(PostNotFoundException.class)
    public String handlePostNotFoundException(PostNotFoundException ex, Model model)
    {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException ex, Model model)
    {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model)
    {
        model.addAttribute("errorMessage", "서버 내부 오류 발생");
        return "error/500";
    }
}
