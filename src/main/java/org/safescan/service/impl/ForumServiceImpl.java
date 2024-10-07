package org.safescan.service.impl;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.ResponseForumDTO;
import org.safescan.mapper.ForumCommentMapper;
import org.safescan.mapper.ForumMapper;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumMapper forumMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Override
    public ResponseForumDTO add(ForumDTO forum) {
        // Put required elements int the object
        LocalDateTime time = LocalDateTime.now();
        forum.setCreateTime(time);
        forum.setUpdateTime(time);

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        forum.setUserId(userId);

        forumMapper.add(forum);
        return forumMapper.getByForum(forum);
    }

    @Override
    public ForumDTO getByForumId(Integer forumId) {
        return forumMapper.getByForumId(forumId);
    }

    @Override
    public ResponseForumDTO getByForum(ForumDTO forum, Integer userId) {
        ResponseForumDTO responseForum = forumMapper.getByForum(forum);
        boolean isLiked = userId != null && forumMapper.isLikedByUserId(userId, forum.getForumId());
        responseForum.setLiked(isLiked);
        return responseForum;
    }

    @Override
    public List<ResponseForumDTO> getForums(int page, int size, Integer userId) {
        int offset = page * size; // Calculating the offset
        List<ResponseForumDTO> responseForums = forumMapper.getForums(offset, size);

        for (ResponseForumDTO forum : responseForums) {
            // If userId is null, it is public list method
            boolean isLiked = userId != null && forumMapper.isLikedByUserId(userId, forum.getForumId());
            forum.setLiked(isLiked);
        }
        return responseForums;
    }

    @Override
    public ResponseForumDTO update(ForumDTO forum) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        forum.setUpdateTime(LocalDateTime.now());
        forumMapper.update(forum);

        ResponseForumDTO responseForum = forumMapper.getByForum(forum);
        boolean isLiked = forumMapper.isLikedByUserId(userId, responseForum.getForumId());
        responseForum.setLiked(isLiked);
        return responseForum;
    }

    @Override
    public void delete(Integer forumId) {
        LocalDateTime updateTime = LocalDateTime.now();
        forumMapper.deleteByForumId(forumId, updateTime);
        forumCommentMapper.deleteByForumCommentId(forumId,null, updateTime);
    }

    @Override
    public ForumDTO getLikedForum(Integer forumId, Integer userId) {
        return forumMapper.getLikedForum(forumId, userId);
    }

    @Override
    public void likeForum(Integer forumId, Integer userId) {
        LocalDateTime time = LocalDateTime.now();

        if (Objects.equals(forumMapper.getStateByForumId(forumId), "Deleted")) {
            throw new RuntimeException("This post has been deleted!");
        }
        forumMapper.addForumLikes(forumId, userId, time);
        forumMapper.updateForumLikes(forumId, time, "like");
    }

    @Override
    public void unlikeForum(Integer forumId, Integer userId) {
        LocalDateTime time = LocalDateTime.now();

        if (Objects.equals(forumMapper.getStateByForumId(forumId), "Deleted")) {
            throw new RuntimeException("This post has been deleted!");
        }
        forumMapper.deleteForumLikes(forumId, userId);
        forumMapper.updateForumLikes(forumId, time, "unlike");
    }


}
