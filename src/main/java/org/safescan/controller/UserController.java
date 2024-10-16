package org.safescan.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.safescan.DTO.AttributesDTO;
import org.safescan.DTO.Result;
import org.safescan.DTO.UserDTO;
import org.safescan.exception.FilePathException;
import org.safescan.service.UserService;
import org.safescan.utils.JwtUtil;
import org.safescan.utils.Md5Util;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${app.uploadAvatars.dir}")
    private String uploadDir;

    @PostMapping("/register")
    public Result<Object> register(@Email(message = "{email.invalid}") @NotEmpty(message = "{email.empty}") String email,
                           @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String password,
                           @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String rePassword) {
        UserDTO registerUser = userService.findByEmail(email);

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
    public Result<String> login(@Email(message = "{email.invalid}") @NotEmpty(message = "{email.empty}") String email,
                        @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String password) {
        // Check weather current user exists
        UserDTO loginUser = userService.findByEmail(email);
        if (loginUser == null){
            return Result.error("Current user is not exists!");
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
            return Result.success("Successfully login!", token);
        }

        // The password is not correct
        return Result.error("Incorrect password!", null);
    }

    @GetMapping("/info")
    public Result<UserDTO> userInfo(){
        Map<String,Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");
        UserDTO user = userService.findByUserId(userId);
        user.setPassword("************"); // Protect user's password
        return Result.success(user);
    }


    @PutMapping("/update")
    public Result<Object> update(@RequestBody @Validated UserDTO newUser){
        // Estimate weather information has been changed
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");
        UserDTO oldUser = userService.findByUserId(userId);
        newUser.setUserId(userId);

        if (Objects.equals(oldUser.getUsername(), newUser.getUsername())
                && Objects.equals(oldUser.getEmail(), newUser.getEmail())) {
            return Result.error("Please modify information in the first beginning!");
        }

        UserDTO testEmailUser = userService.findByEmail(newUser.getEmail());
        if (testEmailUser != null && !Objects.equals(testEmailUser.getUserId(), userId)) {
            return Result.error("Current email address has been register!");
        }

        userService.update(newUser);
        return Result.success("Successfully changed username or email", null);
    }


    @PatchMapping("/update/pwd")
    public Result<Object> updatePassword(
            @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String oldPassword,
            @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String newPassword,
            @Pattern(regexp = "^\\S{5,16}$", message = "{password.rules}") String repPassword) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        UserDTO user = userService.findByUserId(userId);
        if (!Md5Util.hash(oldPassword).equals(user.getPassword())) {  // Incorrect old password
            return Result.error("Incorrect original password!");
        } else if (!Objects.equals(newPassword, repPassword)) {
            return Result.error("These two passwords are not same!");
        } else if (Objects.equals(newPassword, oldPassword)) {
            return Result.error("New password can not be same as original password!");
        }

        userService.updatePassword(userId, newPassword);
        return Result.success("Update Successfully!", null);

    }


    @PatchMapping("/update/avatar")
    public Result<Object> updateAvatar(@RequestParam MultipartFile file) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        if (file.isEmpty()) {
            return Result.error("This is a empty file!");
        }

        try {
            // Make sure the upload directory exists
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                if (!uploadDirFile.mkdirs()) {
                    throw new FilePathException("Failed to create path for avatars!");
                }
            }

            // Generate a unique file name for the file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Get the file storage path
            Path filePath = Paths.get(uploadDirFile.getAbsolutePath(), fileName);
            Files.write(filePath, file.getBytes());

            // Constructing the URL of a file
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            // Save the URL of the file to the database
            userService.updateUserAvatar(userId, fileUrl);

            return Result.success("Avatar uploaded successfully!", fileUrl);

        } catch (IOException e) {
            return Result.error("Failed to upload avatar: " + e.getMessage());
        }
    }

    @GetMapping("/attributes/get")
    public Result<AttributesDTO> getAttributes() {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        AttributesDTO attributes = userService.getAttributes(userId);
        return Result.success("Successfully get attributes by user: #" + userId, attributes);
    }


    @PostMapping("/attributes/put")
    public Result<AttributesDTO> putAttributes(@RequestBody @Validated AttributesDTO attributes) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        userService.setAttributes(userId, attributes);
        return Result.success("Successfully put attributes data into database", attributes);
    }
}