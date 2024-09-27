package org.safescan.service.impl;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.UserForumDTO;
import org.safescan.mapper.ForumMapper;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumMapper forumMapper;

    @Override
    public void add(ForumDTO forumDTO) {
        // Put required elements int the object
        forumDTO.setCreateTime(LocalDateTime.now());
        forumDTO.setUpdateTime(LocalDateTime.now());

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        forumDTO.setUserId(userId);

        forumMapper.add(forumDTO);
    }

    @Override
    public ForumDTO getByForumId(Integer forumId) {
        return forumMapper.getByForumId(forumId);
    }

    @Override
    public List<UserForumDTO> getForums(int page, int size) {
        int offset = page * size; // Calculating the offset
        return forumMapper.getForums(offset, size);
    }

    @Override
    public void update(ForumDTO forumDTO) {
        forumDTO.setUpdateTime(LocalDateTime.now());
        forumMapper.update(forumDTO);
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
    public ForumDTO getLikedForum(Integer forumId, Integer userId) {
        return forumMapper.getLikedForum(forumId, userId);
    }


}
