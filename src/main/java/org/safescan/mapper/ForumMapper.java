package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.ResponseForumDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ForumMapper {
    void add(ForumDTO forum);

    ResponseForumDTO getByForum(ForumDTO forum);

    ForumDTO getByForumId(int forumId);

    List<ResponseForumDTO> getForums(int offset, int size);

    Boolean isLikedByUserId(Integer userId, Integer forumId);

    void update(ForumDTO forum);

    void deleteByForumId(Integer forumId, LocalDateTime updateTime);

    ForumDTO getLikedForum(Integer forumId, Integer userId);

    String getStateByForumId(Integer forumId);

    void addForumLikes(Integer forumId, Integer userId, LocalDateTime createTime);

    void deleteForumLikes(Integer forumId, Integer userId);

    void updateForumLikes(Integer forumId, LocalDateTime updateTime, String state);
}
