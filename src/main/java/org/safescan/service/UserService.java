package org.safescan.service;

import org.safescan.DTO.UserDTO;

public interface UserService {
    UserDTO findByEmail(String email);

    void registerByEmail(String email, String password);

    void update(UserDTO user);

    UserDTO findByUserId(int userId);

    void updateUserAvatar(int userId, String fileUrl);
}
