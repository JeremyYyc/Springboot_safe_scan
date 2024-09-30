package org.safescan.service.impl;

import org.safescan.DTO.UserDTO;
import org.safescan.mapper.UserMapper;
import org.safescan.service.UserService;
import org.safescan.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDTO findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public void registerByEmail(String email, String password) {
        // Encrypt the password
        String encryptedPassword = Md5Util.hash(password);

        // Finish the register
        userMapper.registerByEmail(email, encryptedPassword);
    }

    @Override
    public void update(UserDTO user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public UserDTO findByUserId(int userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public void updateUserAvatar(int userId, String fileUrl) {
        userMapper.updateUserAvatar(userId, fileUrl);
    }
}
