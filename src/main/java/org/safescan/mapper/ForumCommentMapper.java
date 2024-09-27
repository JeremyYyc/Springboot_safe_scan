package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ForumCommentDTO;

import java.time.LocalDateTime;

@Mapper
public interface ForumCommentMapper {
    void addComment(ForumCommentDTO forumComment);

    Integer getForumIdByCommentId(Integer parentCommentId);

    void updateForums(Integer forumId, LocalDateTime updateTime, String state);

    void updateForumComments(Integer parentCommentId, LocalDateTime updateTime, String state);
}
