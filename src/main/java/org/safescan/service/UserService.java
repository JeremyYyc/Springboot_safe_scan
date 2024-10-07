package org.safescan.service;

import org.safescan.DTO.AttributesDTO;
import org.safescan.DTO.UserDTO;

public interface UserService {
    UserDTO findByEmail(String email);

    void registerByEmail(String email, String password);

    void update(UserDTO user);

    UserDTO findByUserId(Integer userId);

    void updateUserAvatar(Integer userId, String fileUrl);

    void updatePassword(Integer userId, String newPassword);

    AttributesDTO getAttributes(Integer userId);

    void setAttributes(Integer userId, AttributesDTO attributes);
}
