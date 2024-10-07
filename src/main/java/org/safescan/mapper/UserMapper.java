package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.AttributesDTO;
import org.safescan.DTO.UserDTO;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    UserDTO findByEmail(String email);

    void registerByEmail(String email, String encryptedPassword);

    void update(UserDTO user);

    UserDTO findByUserId(int userId);

    void updateUserAvatar(int userId, String fileUrl, LocalDateTime updateTime);

    void updatePassword(Integer userId, String encryptedPassword, LocalDateTime updateTime);

    AttributesDTO getAttributes(Integer userId);

    void setAttributes(Integer userId, AttributesDTO attributes);

    void updateAttributes(Integer userId, AttributesDTO attributes);
}
