package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseNotificationDTO {
    Integer userId;
    String state;

    // Notification come from
    Integer fromUserId;
    String fromUserAvatar;
    String fromUsername;

    // Liked or commented information
    Integer forumId;
    Integer commentId;
    Integer ancestorCommentId;

    // If it is a like, nothing is needed. If it is a comment,
    // the comment ID and the content of the comment are required.
    Integer fromCommentId;
    String content;

    String readState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime;
}
