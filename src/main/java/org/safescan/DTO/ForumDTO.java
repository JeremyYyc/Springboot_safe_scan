package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumDTO {

    @NotNull(groups = Update.class)
    private Integer forumId;
    private Integer userId;
    private String title;
    private String content;

    @NotNull
    private int likeCount;
    @NotNull
    private int commentCount;
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public interface Update extends Default {}
}
