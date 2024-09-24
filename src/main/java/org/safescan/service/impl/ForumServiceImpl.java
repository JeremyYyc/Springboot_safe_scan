package org.safescan.service.impl;

import org.safescan.entity.Forum;
import org.safescan.mapper.ForumMapper;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumMapper forumMapper;

    @Override
    public void add(Forum forum) {
        // Put required elements int the object
        forum.setCreateTime(LocalDateTime.now());
        forum.setUpdateTime(LocalDateTime.now());

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        forum.setUserId(userId);

        forumMapper.add(forum);
    }

    @Override
    public Forum getByForumId(Integer forumId) {
        return forumMapper.getByForumId(forumId);
    }

    @Override
    public void update(Forum forum) {
        forum.setUpdateTime(LocalDateTime.now());
        forumMapper.update(forum);
    }

    @Override
    public void delete(Integer forumId) {
        LocalDateTime updateTime = LocalDateTime.now();
        forumMapper.delete(forumId, updateTime);
    }

    @Override
    public void likeForum(Integer forumId, Integer userId) {
        LocalDateTime time = LocalDateTime.now();
        forumMapper.addForumLikes(forumId, userId, time);
        forumMapper.updateForumLikes(forumId, time, "add");
    }

    @Override
    public void unlikeForum(Integer forumId, Integer userId) {
        LocalDateTime time = LocalDateTime.now();
        forumMapper.deleteForumLikes(forumId, userId);
        forumMapper.updateForumLikes(forumId, time, "delete");
    }

    @Override
    public Forum getLikedForum(Integer forumId, Integer userId) {
        return forumMapper.getLikedForum(forumId, userId);
    }


}
