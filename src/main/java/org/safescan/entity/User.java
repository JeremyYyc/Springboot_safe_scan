package org.safescan.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    @NotNull
    private int userId;
    private String username;

    @Email
    @NotEmpty
    private String email;
    private String avatar;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

