package org.safescan.service;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.ResponseForumDTO;

import java.util.List;

public interface ForumService {
    ResponseForumDTO add(ForumDTO forum);

    ForumDTO getByForumId(Integer forumId);

    ResponseForumDTO getByForum(ForumDTO forum, Integer userId);

    List<ResponseForumDTO> getForums(int page, int size, Integer userId);

    ResponseForumDTO update(ForumDTO forum);

    void delete(Integer forumId);

    void likeForum(Integer forumId, Integer userId);

    void unlikeForum(Integer forumId, Integer userId);

    ForumDTO getLikedForum(Integer forumId, Integer userId);
}
