package org.safescan.service;

import org.safescan.DTO.ForumCommentDTO;

public interface ForumCommentService {
    void addComment(ForumCommentDTO forumComment);

    void deleteByCommentId(Integer commentId, Integer userId);

    ForumCommentDTO getByCommentId(Integer commentId);

    ForumCommentDTO getLikeComment(Integer commentId);

    void likeForum(Integer commentId);

    void unlikeForum(Integer commentId);
}
