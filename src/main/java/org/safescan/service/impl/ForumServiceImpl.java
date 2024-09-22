package org.safescan.service.impl;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.UserForumDTO;
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
    public void add(ForumDTO forum) {
        // Put required elements int the object
        forum.setCreateTime(LocalDateTime.now());
        forum.setUpdateTime(LocalDateTime.now());

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        forum.setUserId(userId);

        forumMapper.add(forum);
    }

    @Override
    public ForumDTO getByForumId(Integer forumId) {
        return forumMapper.getByForumId(forumId);
    }

    @Override
    public List<UserForumDTO> getForums(int page, int size, Integer userId) {
        int offset = page * size; // Calculating the offset
        List<UserForumDTO> userForums = forumMapper.getForums(offset, size);

        for (UserForumDTO userForum : userForums) {
            // If userId is null, it is public list method
            boolean isLiked = userId != null && forumMapper.isLikedByUserId(userId, userForum.getForumId());
            userForum.setLiked(isLiked);
        }
        return userForums;
    }

    @Override
    public void update(ForumDTO forum) {
        forum.setUpdateTime(LocalDateTime.now());
        forumMapper.update(forum);
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
