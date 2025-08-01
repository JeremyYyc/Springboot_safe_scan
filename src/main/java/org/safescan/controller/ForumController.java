package org.safescan.controller;

import org.safescan.DTO.ForumDTO;
import org.safescan.DTO.Result;
import org.safescan.DTO.ResponseForumDTO;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/forum")
@Validated
public class ForumController {
    @Autowired
    private ForumService forumService;

    @PostMapping("/add")
    public Result<ResponseForumDTO> add(@RequestBody @Validated ForumDTO forum) {
        ResponseForumDTO post = forumService.add(forum);
        return Result.success("Successfully posting this post!", post);
    }


    @GetMapping("/public/detail")
    public Result<ResponseForumDTO> getForum(@RequestParam Integer forumId) {
        ForumDTO forum = new ForumDTO();
        forum.setForumId(forumId);
        ResponseForumDTO responseForum = forumService.getByForum(forum, null);
        return Result.success("Successfully get detail of corresponding forum without token", responseForum);
    }


    @GetMapping("/private/detail")
    public Result<ResponseForumDTO> getForumByUser(@RequestParam Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        ForumDTO forum = new ForumDTO();
        forum.setForumId(forumId);
        ResponseForumDTO responseForum = forumService.getByForum(forum, userId);
        return Result.success("Successfully get detail of corresponding forum with token", responseForum);
    }


    @GetMapping("/public/get")
    public Result<List<ResponseForumDTO>> getForums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ResponseForumDTO> forums = forumService.getForums(page, size, null);
        return Result.success("Successfully get forums without token!" ,forums);
    }


    @GetMapping("/private/get")
    public Result<List<ResponseForumDTO>> getForumsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        List<ResponseForumDTO> forums = forumService.getForums(page, size, userId);
        return Result.success("Successfully get forums with token!", forums);
    }


    @PutMapping("/update")
    public Result<ResponseForumDTO> update(@RequestBody @Validated(ForumDTO.Update.class) ForumDTO forumDTO) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        ForumDTO validatedForum = forumService.getByForumId(forumDTO.getForumId());
        if (validatedForum == null) {
            return Result.error("This post has been deleted!");
        } else if (!Objects.equals(validatedForum.getUserId(), userId)) {
            return Result.error("You are not allowed to update other user's post!");
        }

        ResponseForumDTO forum = forumService.update(forumDTO);
        return Result.success("Successfully update the post!", forum);
    }


    @PutMapping("/delete")
    public Result<Object> delete(Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        ForumDTO validatedForum = forumService.getByForumId(forumId);
        if (validatedForum == null) {
            return Result.error("This post has been deleted!");
        } else if (!Objects.equals(validatedForum.getUserId(), userId)) {
            return Result.error("You are not allowed to delete other user's post!");
        }

        forumService.delete(forumId);
        return Result.success("Successfully delete the post!", null);
    }


    @PostMapping("/like")
    public Result<Object> like(@RequestParam Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // To check weather this post has been liked by this user
        ForumDTO forum = forumService.getLikedForum(forumId, userId);

        if (userId == null) {
            return Result.error("Please log in to like this post!");
        } else if (forum != null) {
            return Result.error("You have already liked this post!");
        }

        forumService.likeForum(forumId, userId);
        return Result.success("Successfully liked the post!", null);
    }


    @PostMapping("/unlike")
    public Result<Object> unlike(@RequestParam Integer forumId){
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // To check weather this post has been liked by this user
        ForumDTO forum = forumService.getLikedForum(forumId, userId);

        if (userId == null) {
            return Result.error("Please log in to unlike this post!");
        } else if (forum == null) {
            return Result.error("You have not liked this post yet!");
        }

        forumService.unlikeForum(forumId, userId);
        return Result.success("Successfully unliked the post!", null);
    }
}
