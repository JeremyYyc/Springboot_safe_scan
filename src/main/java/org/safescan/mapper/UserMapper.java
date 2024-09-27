package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.UserDTO;

@Mapper
public interface UserMapper {
    UserDTO findByEmail(String email);

    void registerByEmail(String email, String encryptedPassword);

    void update(UserDTO userDTO);

    UserDTO findByUserId(int userId);
}
