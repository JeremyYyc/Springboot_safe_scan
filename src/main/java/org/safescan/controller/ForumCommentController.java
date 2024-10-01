package org.safescan.controller;

import org.safescan.DTO.Result;
import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.ResponseCommentDTO;
import org.safescan.service.ForumCommentService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/comment")
@Validated
public class ForumCommentController {
    @Autowired
    private ForumCommentService forumCommentService;

    @PostMapping("/add")
    public Result<ResponseCommentDTO> addComments(@RequestBody @Validated ForumCommentDTO forumComment) {
        ResponseCommentDTO comment = forumCommentService.addComment(forumComment);
        String message = "Successfully comment this " +
                (forumComment.getParentCommentId() == null ? "post!" : "comment!");
        return Result.success(message, comment);
    }


    @PutMapping("/delete")
    public Result<Object> delete(Integer commentId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        ForumCommentDTO validatedComment = forumCommentService.getByCommentId(commentId);
        if (validatedComment == null) {
            return Result.error("Unable to find this comment!");
        } else if (Objects.equals(validatedComment.getState(), "Deleted")) {
            return Result.error("This comment has been deleted!");
        } else if (!Objects.equals(validatedComment.getUserId(), userId)) {
            return Result.error("You are not allowed to delete other user's comment!");
        }

        forumCommentService.deleteByCommentId(commentId, userId);
        return Result.success("Successfully deleted this comment!", null);
    }


    @PostMapping("/like")
    public Result<Object> like(@RequestParam Integer commentId) {
        // To check weather this post has been liked by this user
        ForumCommentDTO comment = forumCommentService.getLikeComment(commentId);
        if (comment != null) {
            return Result.error("You have already liked this comment!", null);
        }
        forumCommentService.likeForum(commentId);
        return Result.success("Successfully liked the comment!", null);
    }


    @PostMapping("/unlike")
    public Result<Object> unlike(@RequestParam Integer commentId) {
        // To check weather this post has been liked by this user
        ForumCommentDTO comment = forumCommentService.getLikeComment(commentId);
        if (comment == null) {
            return Result.error("You have not liked this comment yet!");
        }

        forumCommentService.unlikeForum(commentId);
        return Result.success("Successfully unliked the comment!", null);
    }


    @GetMapping("/public/get")
    public Result<List<ResponseCommentDTO>> getComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam Integer forumId) {
        List<ResponseCommentDTO> comments = forumCommentService.getComments(page, size,
                null, forumId, null);
        return Result.success(comments);
    }


    @GetMapping("/private/get")
    public Result<List<ResponseCommentDTO>> getCommentsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam Integer forumId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        List<ResponseCommentDTO> comments = forumCommentService.getComments(page, size,
                userId, forumId, null);
        return Result.success(comments);
    }


    @GetMapping("/public/get/son")
    public Result<List<ResponseCommentDTO>> getSonComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam Integer forumId,
            @RequestParam Integer ancestorCommentId) {
        List<ResponseCommentDTO> comments = forumCommentService.getComments(page, size,
                null, forumId, ancestorCommentId);
        return Result.success(comments);
    }


    @GetMapping("private/get/son")
    public Result<List<ResponseCommentDTO>> getSonCommentsByUser (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam Integer forumId,
            @RequestParam Integer ancestorCommentId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");

        List<ResponseCommentDTO> comments = forumCommentService.getComments(page, size,
                userId, forumId, ancestorCommentId);
        return Result.success(comments);
    }
}
