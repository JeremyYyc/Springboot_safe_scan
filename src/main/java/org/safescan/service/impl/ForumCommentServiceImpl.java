package org.safescan.service.impl;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.mapper.ForumCommentMapper;
import org.safescan.service.ForumCommentService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

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

        // Obtain the commentId by parentCommentId if commentId is null
        if (forumComment.getForumId() == null){
            Integer forumId = forumCommentMapper.getForumIdByCommentId(forumComment.getParentCommentId());
            forumComment.setForumId(forumId);
        }

        // Store this message information into the database
        forumComment.setUserId(userId);
        forumComment.setCreateTime(time);
        forumComment.setUpdateTime(time);
        forumCommentMapper.addComment(forumComment);

        // Update comment information in database table comments and forumComments under different situations
        // Add comment count in forums for both situation
        forumCommentMapper.updateForums(forumComment.getForumId(), time, "comment");

        if (forumComment.getParentCommentId() != null) {
            // Add comment count in forum_comment when it is a son comment of a parent comment
            forumCommentMapper.updateForumComments(forumComment.getParentCommentId(), time, "comment");
        }
    }
}
