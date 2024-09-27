package org.safescan.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.safescan.DTO.Result;
import org.safescan.DTO.UserDTO;
import org.safescan.service.UserService;
import org.safescan.utils.JwtUtil;
import org.safescan.utils.Md5Util;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Email(message = "{email.invalid}") @NotEmpty(message = "{email.empty}") String email,
                           @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String password,
                           @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String rePassword) {
        UserDTO registerUserDTO = userService.findByEmail(email);

        if (!password.equals(rePassword)) {
            return Result.error("Please enter the same rePassword as password!");
        } else if (registerUserDTO == null) {
            userService.registerByEmail(email, password);
            return Result.success("Successfully register!", null);
        } else {
            return Result.error("Current email has been registered");
        }
    }

    @PostMapping("login")
    public Result login(@Email(message = "{email.invalid}") @NotEmpty(message = "{email.empty}") String email,
                        @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String password) {
        // Check weather current user exists
        UserDTO loginUserDTO = userService.findByEmail(email);
        if (loginUserDTO == null){
            return Result.error("Current user is not exists!");
        }

        // Check weather the password is correct
        if (Md5Util.hash(password).equals(loginUserDTO.getPassword())){
            // Generate JWT token
            Map<String, Object> claims = new HashMap<>();

            // There are only two key-value pairs: userId and email,
            // so there are only these two in map of ThreadLocalUtils
            claims.put("userId", loginUserDTO.getUserId());
            claims.put("email", loginUserDTO.getEmail());
            String token = JwtUtil.generateToken(claims);

            // Store token into redis
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,2, TimeUnit.HOURS);
            return Result.success("Successfully login!", token);
        }

        // The password is not correct
        return Result.error("Incorrect password!");
    }

    @GetMapping("/info")
    public Result<UserDTO> userInfo(){
        Map<String,Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");
        UserDTO userDTO = userService.findByUserId(userId);
        return Result.success(userDTO);
    }


    @PutMapping("/update")
    public Result update(@RequestBody @Validated UserDTO userDTO){
        // Estimate weather information has been changed
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");
        UserDTO oldUserDTO = userService.findByUserId(userId);

        if (Objects.equals(oldUserDTO.getUsername(), userDTO.getUsername())
                && Objects.equals(oldUserDTO.getEmail(), userDTO.getEmail())) {
            return Result.error("Please modify information in the first beginning!");
        }

        UserDTO testEmailUserDTO = userService.findByEmail(userDTO.getEmail());
        if (testEmailUserDTO != null && !Objects.equals(testEmailUserDTO.getUserId(), userDTO.getUserId())) {
            return Result.error("Current email address has been register!");
        }

        userService.update(userDTO);
        return Result.success();
    }
}