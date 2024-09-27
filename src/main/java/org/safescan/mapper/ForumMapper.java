package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.UserForumDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ForumMapper {
    void add(ForumDTO forumDTO);

    ForumDTO getByForumId(int forumId);

    List<UserForumDTO> getForums(int offset, int size);

    void update(ForumDTO forumDTO);

    void delete(Integer forumId, LocalDateTime updateTime);

    void addForumLikes(Integer forumId, Integer userId, LocalDateTime createTime);

    void deleteForumLikes(Integer forumId, Integer userId);

    void updateForumLikes(Integer forumId, LocalDateTime updateTime, String state);

    ForumDTO getLikedForum(Integer forumId, Integer userId);
}
