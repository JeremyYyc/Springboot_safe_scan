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
    public void addComment(ForumCommentDTO forumCommentDTO) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        // Ensure create time and update time are same
        LocalDateTime time = LocalDateTime.now();

        // Obtain the commentId by parentCommentId if commentId is null
        if (forumCommentDTO.getForumId() == null){
            Integer forumId = forumCommentMapper.getForumIdByCommentId(forumCommentDTO.getParentCommentId());
            forumCommentDTO.setForumId(forumId);
        }

        // Store this message information into the database
        forumCommentDTO.setUserId(userId);
        forumCommentDTO.setCreateTime(time);
        forumCommentDTO.setUpdateTime(time);
        forumCommentMapper.addComment(forumCommentDTO);

        // Update comment information in database table comments and forumComments under different situations
        // Add comment count in forums for both situation
        forumCommentMapper.updateForums(forumCommentDTO.getForumId(), time, "comment");

        if (forumCommentDTO.getParentCommentId() != null) {
            // Add comment count in forum_comment when it is a son comment of a parent comment
            forumCommentMapper.updateForumComments(forumCommentDTO.getParentCommentId(), time, "comment");
        }
    }
}
