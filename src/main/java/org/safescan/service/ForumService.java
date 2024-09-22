package org.safescan.service;

import org.safescan.entity.Forum;

public interface ForumService {
    void add(Forum forum);

    Forum getByForumId(Integer forumId);

    void update(Forum forum);

    void delete(Integer forumId);

    void likeForum(Integer forumId);

//    void syncDataToDatabase();
}
