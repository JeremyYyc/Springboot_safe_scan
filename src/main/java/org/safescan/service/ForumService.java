package org.safescan.service;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.UserForumDTO;

import java.util.List;

public interface ForumService {
    void add(ForumDTO forumDTO);

    ForumDTO getByForumId(Integer forumId);

    List<UserForumDTO> getForums(int page, int size);

    void update(ForumDTO forumDTO);

    void delete(Integer forumId);

    void likeForum(Integer forumId, Integer userId);

    void unlikeForum(Integer forumId, Integer userId);

    ForumDTO getLikedForum(Integer forumId, Integer userId);
}
