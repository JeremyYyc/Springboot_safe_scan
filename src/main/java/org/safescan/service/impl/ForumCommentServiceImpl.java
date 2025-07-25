package org.safescan.service.impl;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.ResponseCommentDTO;
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
    public ResponseCommentDTO addComment(ForumCommentDTO forumComment) {
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
        forumCommentMapper.updateForums(forumComment.getForumId(), time, "comment", 1);

        if (forumComment.getParentCommentId() != null) {
            // Add comment count in forum_comment when it is a son comment of a parent comment
            forumCommentMapper.updateForumComments(forumComment.getParentCommentId(),
                   forumComment.getAncestorCommentId(), time, "comment");
        }

        return forumCommentMapper.getByComment(forumComment);
    }

    @Override
    public ForumCommentDTO getByCommentId(Integer commentId) {
        return forumCommentMapper.getByCommentId(commentId);
    }

    @Override
    public void deleteByCommentId(Integer commentId, Integer userId) {
        try {
            LocalDateTime time = LocalDateTime.now();
            ForumCommentDTO forum = forumCommentMapper.getByCommentId(commentId);

            // Delete a parent comment & a sub comment
            forumCommentMapper.deleteByForumCommentId(commentId, userId, time);

            // Update comment counts in database whenever delete a parent comment or sub comment
            forumCommentMapper.updateForums(forum.getForumId(), LocalDateTime.now(),
                    "delete",1);

            // Delete all sub comments of this parent comment (ancestor comment)
            // If it's a sub comment, no records would be deleted at this time
            int deletedSubCommentCounts = forumCommentMapper.deleteByAncestorCommentId(commentId, time);
            forumCommentMapper.updateForums(forum.getForumId(), LocalDateTime.now(),
                    "delete", deletedSubCommentCounts);     // Update its forum record

            // If this comment is a sub comment
            if (forum.getAncestorCommentId() != null) {
                // Update comment counts in database when delete a sub comment under its ancestor comment record
                forumCommentMapper.updateForumComments(forum.getParentCommentId(), forum.getAncestorCommentId(),
                        LocalDateTime.now(), "delete");
            }
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
    public List<ResponseCommentDTO> getComments(
            int page, int size,
            Integer userId, Integer forumId, Integer ancestorCommentId) {
        int offset = page * size; // Calculating the offset

        List<ResponseCommentDTO> comments;
        if (ancestorCommentId == null) {
            comments = forumCommentMapper.getParentComments(offset, size, forumId);
        } else {
            comments = forumCommentMapper.getSonComments(offset, size, forumId, ancestorCommentId);
        }


        for (ResponseCommentDTO comment: comments) {
            boolean isLiked = userId != null && forumCommentMapper.isLikedByUser(userId, comment.getCommentId());
            comment.setLiked(isLiked);
        }

        return comments;
    }
}
