package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.entity.User;

@Mapper
public interface UserMapper {
    User findByEmail(String email);

    void registerByEmail(String email, String encryptedPassword);

    void update(User user);

    User findByUserId(int userId);
}
