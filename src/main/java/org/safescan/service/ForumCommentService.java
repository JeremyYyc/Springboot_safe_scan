package org.safescan.service;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.ResponseCommentDTO;

import java.util.List;

public interface ForumCommentService {
    ResponseCommentDTO addComment(ForumCommentDTO forumComment);

    void deleteByCommentId(Integer commentId, Integer userId);

    ForumCommentDTO getByCommentId(Integer commentId);

    ForumCommentDTO getLikeComment(Integer commentId);

    void likeForum(Integer commentId);

    void unlikeForum(Integer commentId);

    List<ResponseCommentDTO> getComments(int page, int size, Integer userId, Integer forumId, Integer ancestorCommentId);
}
