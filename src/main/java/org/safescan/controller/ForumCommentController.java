package org.safescan.controller;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.Result;
import org.safescan.service.ForumCommentService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/comment")
@Validated
public class ForumCommentController {
    @Autowired
    private ForumCommentService forumCommentService;

    @PostMapping("/add")
    public Result addComments(@RequestBody @Validated ForumCommentDTO forumComment) {
        forumCommentService.addComment(forumComment);
        String message = "Successfully comment this " +
                (forumComment.getParentCommentId() == null ? "post!" : "comment!");
        return Result.success(message, null);
    }

    @PutMapping("/delete")
    public Result delete(Integer commentId) {
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
        return Result.success("Successfully deleted this comment!");
    }

    @PostMapping("/like")
    public Result like(@RequestParam Integer commentId) {
        // To check weather this post has been liked by this user
        ForumCommentDTO comment = forumCommentService.getLikeComment(commentId);
        if (comment != null) {
            return Result.error("You have already liked this comment!");
        }
        forumCommentService.likeForum(commentId);
        return Result.success("Successfully liked the comment!", null);
    }


    @PostMapping("/unlike")
    public Result unlike(@RequestParam Integer commentId){
        // To check weather this post has been liked by this user
        ForumCommentDTO comment = forumCommentService.getLikeComment(commentId);
        if (comment == null) {
            return Result.error("You have not liked this comment yet!");
        }

        forumCommentService.unlikeForum(commentId);
        return Result.success("Successfully unliked the comment!", null);
    }
}
