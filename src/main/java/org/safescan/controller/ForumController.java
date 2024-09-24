package org.safescan.controller;

import org.safescan.entity.Forum;
import org.safescan.entity.Result;
import org.safescan.service.ForumService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forum")
@Validated
public class ForumController {
    @Autowired
    private ForumService forumService;

    @PostMapping("/add")
    public Result add(@RequestBody @Validated Forum forum) {
        forumService.add(forum);
        return Result.success("Successfully posting this post!", null);
    }


    @GetMapping("/detail")
    public Result<Forum> detail(Integer forumId) {
        Forum forum = forumService.getByForumId(forumId);
        return Result.success(forum);
    }


    @PutMapping("/update")
    public Result update(@RequestBody @Validated(Forum.Update.class) Forum forum) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        if (forumService.getByForumId(forum.getForumId()).getUserId() != userId) {
            return Result.error("You are not allowed to update other user's post!");
        }

        forumService.update(forum);
        return Result.success("Successfully update the post!", null);
    }


    @PutMapping("/delete")
    public Result delete(Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        if (forumService.getByForumId(forumId).getUserId() != userId) {
            return Result.error("You are not allowed to delete other user's post!");
        }

        forumService.delete(forumId);
        return Result.success("Successfully delete the post!", null);
    }


    @PostMapping("/like")
    public Result like(@RequestParam Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // To check weather this post has been liked by this user
        Forum forum = forumService.getLikedForum(forumId, userId);

        if (userId == null) {
            return Result.error("Please log in to like this post!");
        } else if (forum != null) {
            return Result.error("You have already liked this post!");
        }

        forumService.likeForum(forumId, userId);
        return Result.success("Successfully liked the post!", null);
    }


    @PostMapping("/unlike")
    public Result unlike(@RequestParam Integer forumId){
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        // To check weather this post has been liked by this user
        Forum forum = forumService.getLikedForum(forumId, userId);

        if (userId == null) {
            return Result.error("Please log in to unlike this post!");
        } else if (forum == null) {
            return Result.error("You have not liked this post yet!");
        }

        forumService.unlikeForum(forumId, userId);
        return Result.success("Successfully unliked the post!", null);
    }


    @GetMapping("/hasLiked")
    public Result hasLiked(@RequestParam Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        Forum forum = forumService.getLikedForum(forumId, userId);
        if (forum != null) {
            return Result.success("YES", null);
        } else {
            return Result.error("NO");
        }
    }
}
