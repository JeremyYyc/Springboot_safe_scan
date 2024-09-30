package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserForumDTO {
    private Integer userId;
    private String username;
    private String avatar;
    private String title;
    private String content;
    private Integer forumId;
    private boolean isLiked;

    @NotNull
    private int likeCount;
    @NotNull
    private int commentCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
