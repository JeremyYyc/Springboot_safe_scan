package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseCommentDTO {
    private Integer commentId;
    private Integer userId;
    private String username;
    private String avatar;
    private String content;
    private Integer ancestorCommentId;

    // If this comment is on another comment, it would have these attributes.
    // If not, these attributes are null.
    private Integer parentUserId;
    private String parentUsername;
    private String parentAvatar;

    @NotNull
    private int likeCount;
    @NotNull
    private int commentCount;
    private boolean isLiked;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
