package org.safescan.service;

import org.safescan.entity.User;

public interface UserService {
    User findByEmail(String email);

    void registerByEmail(String email, String password);
}
