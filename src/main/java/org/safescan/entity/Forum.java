package org.safescan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Forum {

    @NotNull(groups = Update.class)
    private int forumId;
    private int userId;
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
