package com.example.my_server.controller;

import com.example.my_server.domain.User;
import com.example.my_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController
{
    @Autowired
    private UserService userService;

    @GetMapping("/user/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model)
    {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "user/login";
    }

    @GetMapping("/user/join")
    public String joinForm()
    { return "user/join"; }

    @PostMapping("/user/joinProc")
    public String joinProc(User user)
    {
        if (userService.checkLoginIdDuplicate(user.getLoginId()))
        { return "redirect:/user/join?error=id_duplicate"; }

        if (userService.checkUsernameDuplicate(user.getNickname()))
        { return "redirect:/user/join?error=name_duplicate"; }
        userService.join(user);
        return "redirect:/user/login";
    }
}
