package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDTO {
    Integer chatId;
    Integer userId;
    String question;
    String reply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public ChatDTO(String question, String reply, Integer userId) {
        this.question = question;
        this.reply = reply;
        this.userId = userId;
        this.createTime = LocalDateTime.now();
    }
}
