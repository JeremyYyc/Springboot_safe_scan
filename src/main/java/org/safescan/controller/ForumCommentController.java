package org.safescan.controller;

import org.safescan.DTO.ForumCommentDTO;
import org.safescan.DTO.Result;
import org.safescan.service.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@Validated
public class ForumCommentController {
    @Autowired
    private ForumCommentService forumCommentService;

    @PostMapping
    public Result addComments(@RequestBody @Validated ForumCommentDTO forumCommentDTO) {
        forumCommentService.addComment(forumCommentDTO);
        String message = "Successfully comment this " +
                (forumCommentDTO.getParentCommentId() == null ? "post!" : "comment!");
        return Result.success(message, null);
    }
}
