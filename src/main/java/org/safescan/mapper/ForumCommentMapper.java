package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.ResponseCommentDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ForumCommentMapper {
    void addComment(ForumCommentDTO forumComment);

    ResponseCommentDTO getByComment(ForumCommentDTO forumComment);

    List<ResponseCommentDTO> getParentComments(int offset, int size, Integer forumId);

    List<ResponseCommentDTO> getSonComments(int offset, int size, Integer forumId, Integer ancestorCommentId);

    boolean isLikedByUser(Integer userId, Integer commentId);

    String getState(Integer forumId, Integer parentCommentId);

    String getStateByCommentId(Integer commentId);

    void updateForums(Integer forumId, LocalDateTime updateTime, String state, Integer counts);

    void updateForumComments(Integer parentCommentId, Integer ancestorCommentId, LocalDateTime updateTime, String state);

    ForumCommentDTO getByCommentId(Integer commentId);

    void deleteByForumCommentId(Integer commentId, Integer userId, LocalDateTime updateTime);

    int deleteByAncestorCommentId(Integer commentId, LocalDateTime updateTime);

    ForumCommentDTO getLikedComment(Integer commentId, Integer userId);

    void addForumLikes(Integer commentId, Integer userId, LocalDateTime createTime);

    void deleteForumLikes(Integer commentId, Integer userId);

    void updateForumLikes(Integer commentId, LocalDateTime updateTime, String state);
}
