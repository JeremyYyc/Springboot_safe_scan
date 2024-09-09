package org.safescan.service.impl;

import org.safescan.entity.User;
import org.safescan.mapper.UserMapper;
import org.safescan.service.UserService;
import org.safescan.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public void registerByEmail(String email, String password) {
        // Encrypt the password
        String encryptedPassword = Md5Util.hash(password);

        // Finish the register
        userMapper.registerByEmail(email, encryptedPassword);
    }
}
