package org.safescan.controller;

import org.safescan.entity.Result;
import org.safescan.entity.User;
import org.safescan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Validated String email, String password, String rePassword) {
        User user = userService.findByEmail(email);

        if (!password.equals(rePassword)) {
            return Result.error("Please enter the same rePassword as password!");
        } else if (user == null) {
            userService.registerByEmail(email, password);
            return Result.success("Successfully register!", null);
        } else {
            return Result.error("Current email has been registered");
        }
    }


}