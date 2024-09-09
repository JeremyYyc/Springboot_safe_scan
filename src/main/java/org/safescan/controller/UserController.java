package org.safescan.controller;

import org.safescan.entity.Result;
import org.safescan.entity.User;
import org.safescan.service.UserService;
import org.safescan.utils.JwtUtil;
import org.safescan.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Validated String email, String password, String rePassword) {
        User registerUser = userService.findByEmail(email);

        if (!password.equals(rePassword)) {
            return Result.error("Please enter the same rePassword as password!");
        } else if (registerUser == null) {
            userService.registerByEmail(email, password);
            return Result.success("Successfully register!", null);
        } else {
            return Result.error("Current email has been registered");
        }
    }

    @PostMapping("login")
    public Result login(String email, String password) {
        // Check weather current user exists
        User loginUser = userService.findByEmail(email);
        if (loginUser == null){
            return Result.error("Current user is not exists");
        }

        // Check weather the password is correct
        if (Md5Util.hash(password).equals(loginUser.getPassword())){
            // Generate JWT token
            Map<String, Object> claims = new HashMap<>();

            // There are only two key-value pairs: userId and email,
            // so there are only these two in map of ThreadLocalUtils
            claims.put("userId", loginUser.getUserId());
            claims.put("email", loginUser.getEmail());
            String token = JwtUtil.generateToken(claims);

            // Store token into redis
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,2, TimeUnit.HOURS);
            return Result.success("Successfully login", token);
        }

        // The password is not correct
        return Result.error("Incorrect password!");
    }


}