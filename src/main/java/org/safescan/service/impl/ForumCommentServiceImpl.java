package org.safescan.service.impl;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.UserCommentDTO;
import org.safescan.mapper.ForumCommentMapper;
import org.safescan.service.ForumCommentService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ForumCommentServiceImpl implements ForumCommentService {
    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Override
    public void addComment(ForumCommentDTO forumComment) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        // Ensure create time and update time are same
        LocalDateTime time = LocalDateTime.now();
        Integer forumId = forumComment.getForumId();
        Integer parentCommentId = forumComment.getParentCommentId();
        Integer ancestorCommentId = forumComment.getAncestorCommentId(); // Default value when it is a parent comment

        // Obtain the commentId by parentCommentId if it is a comment to another comment
        if (forumId == null && parentCommentId != null){ // Front-end gives parentCommentId instead of forumId
            ForumCommentDTO parentComment = forumCommentMapper.getByCommentId(parentCommentId);
            // forumId = forumCommentMapper.getForumIdByCommentId(parentCommentId);
            if (parentComment == null) {
                throw new RuntimeException("Unable to find the post by current comment id!");
            }

            forumId = parentComment.getForumId();
            ancestorCommentId = parentComment.getAncestorCommentId() == null // It's parent comment is ancestor comment
                    ? parentCommentId
                    : parentComment.getAncestorCommentId();
        }

        // Estimate weather this comment is deleted or not
        if (Objects.equals(forumCommentMapper.getState(forumId, parentCommentId), "Deleted")){
            throw new RuntimeException("This "+ (parentCommentId == null ? "post" : "comment" ) +" has been deleted!");
        }

        // Store this message information into the database
        forumComment.setForumId(forumId);
        forumComment.setAncestorCommentId(ancestorCommentId);
        forumComment.setUserId(userId);
        forumComment.setCreateTime(time);
        forumComment.setUpdateTime(time);

        try {
            forumCommentMapper.addComment(forumComment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add this comment! Possible reason: There is no target post!");
        }


        // Update comment information in database table comments and forumComments under different situations
        // Add comment count in forums for both situation
        forumCommentMapper.updateForums(forumComment.getForumId(), time, "comment");

        if (forumComment.getParentCommentId() != null) {
            // Add comment count in forum_comment when it is a son comment of a parent comment
            forumCommentMapper.updateForumComments(forumComment.getParentCommentId(), time, "comment");
        }
    }

    @Override
    public ForumCommentDTO getByCommentId(Integer commentId) {
        return forumCommentMapper.getByCommentId(commentId);
    }

    @Override
    public void deleteByCommentId(Integer commentId, Integer userId) {
        try {
            forumCommentMapper.deleteByForumCommentId(commentId, userId, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Unable to find this comment!");
        }
    }

    @Override
    public ForumCommentDTO getLikeComment(Integer commentId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        return forumCommentMapper.getLikedComment(commentId, userId);
    }

    @Override
    public void likeForum(Integer commentId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        if (Objects.equals(forumCommentMapper.getStateByCommentId(commentId), "Deleted")) {
            throw new RuntimeException("This comment has been deleted!");
        }

        LocalDateTime time = LocalDateTime.now();
        forumCommentMapper.addForumLikes(commentId, userId, time);
        forumCommentMapper.updateForumLikes(commentId, time, "like");
    }

    @Override
    public void unlikeForum(Integer commentId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        if (Objects.equals(forumCommentMapper.getStateByCommentId(commentId), "Deleted")) {
            throw new RuntimeException("This comment has been deleted!");
        }

        LocalDateTime time = LocalDateTime.now();
        forumCommentMapper.deleteForumLikes(commentId, userId);
        forumCommentMapper.updateForumLikes(commentId, time, "unlike");
    }

    @Override
    public List<UserCommentDTO> getComments(
            int page, int size,
            Integer userId, Integer forumId, Integer ancestorCommentId) {
        int offset = page * size; // Calculating the offset

        List<UserCommentDTO> userComments;
        if (ancestorCommentId == null) {
            userComments = forumCommentMapper.getParentComments(offset, size, forumId);
        } else {
            userComments = forumCommentMapper.getSonComments(offset, size, ancestorCommentId);
        }


        for (UserCommentDTO userComment: userComments) {
            boolean isLiked = userId != null && forumCommentMapper.isLikedByUser(userId, userComment.getCommentId());
            userComment.setLiked(isLiked);
        }

        return userComments;
    }
}
