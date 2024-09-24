package org.safescan.service;

import org.safescan.entity.Forum;

public interface ForumService {
    void add(Forum forum);

    Forum getByForumId(Integer forumId);

    void update(Forum forum);

    void delete(Integer forumId);

    void likeForum(Integer forumId, Integer userId);

    void unlikeForum(Integer forumId, Integer userId);

    Forum getLikedForum(Integer forumId, Integer userId);
}
