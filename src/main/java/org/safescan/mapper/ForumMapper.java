package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.entity.Forum;

import java.time.LocalDateTime;

@Mapper
public interface ForumMapper {
    void add(Forum forum);

    Forum getByForumId(int forumId);

    void update(Forum forum);

    void delete(Integer forumId, LocalDateTime updateTime);
}
