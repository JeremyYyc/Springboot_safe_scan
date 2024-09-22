package org.safescan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumComment {
    @NotNull
    private int commentId;
    private int userId;
    private int forumId;
    private String content;
    @NotNull
    private int likeCount;
    @NotNull
    private int commentCount;
    private int parentCommentId;
    private String state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
