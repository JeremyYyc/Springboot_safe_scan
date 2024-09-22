package org.safescan.service.impl;

import org.safescan.entity.Forum;
import org.safescan.mapper.ForumMapper;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumMapper forumMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String FORUM_LIKES_KEY = "forum_likes:";
    private static final String FORUM_COMMENTS_KEY = "forum_comments:";

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
    public void likeForum(Integer forumId) {
        String key = FORUM_LIKES_KEY + forumId;
        stringRedisTemplate.opsForValue().increment(key);
    }

//    @Override
//    public void syncDataToDatabase() {
//        List<Forum> forums = forumMapper.getAllForums();
//        for (Forum forum : forums) {
//            String likesKey = FORUM_LIKES_KEY + forum.getForumId();
//            String commentsKey = FORUM_COMMENTS_KEY + forum.getForumId();
//
//            String likesCount = stringRedisTemplate.opsForValue().get(likesKey);
//            String commentsCount = stringRedisTemplate.opsForValue().get(commentsKey);
//
//            if (likesCount != null) {
//                forum.setLikeCount(Integer.parseInt(likesCount));
//            }
//            if (commentsCount != null) {
//                forum.setCommentCount(Integer.parseInt(commentsCount));
//            }
//
//            forumMapper.updateForumCounts(forum);
//        }
//    }
}
