package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.entity.User;

@Mapper
public interface UserMapper {
    User findByEmail(String email);

//    @Insert("insert into user(email, password, creat_time, update_time)\n" +
//            "values(#{email}, #{encryptedPassword}, now(), now());")
    void registerByEmail(String email, String encryptedPassword);
}
