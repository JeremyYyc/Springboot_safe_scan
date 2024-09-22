package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ForumCommentDTO;

import java.time.LocalDateTime;

@Mapper
public interface ForumCommentMapper {
    void addComment(ForumCommentDTO forumComment);

    Integer getForumIdByCommentId(Integer parentCommentId);

    String getState(Integer forumId, Integer parentCommentId);

    String getStateByCommentId(Integer commentId);

    void updateForums(Integer forumId, LocalDateTime updateTime, String state);

    void updateForumComments(Integer parentCommentId, LocalDateTime updateTime, String state);

    ForumCommentDTO getByCommentId(Integer commentId);

    void deleteByForumCommentId(Integer commentId, Integer userId, LocalDateTime updateTime);

    ForumCommentDTO getLikedComment(Integer commentId, Integer userId);

    void addForumLikes(Integer commentId, Integer userId, LocalDateTime createTime);

    void deleteForumLikes(Integer commentId, Integer userId);

    void updateForumLikes(Integer commentId, LocalDateTime updateTime, String state);
}
