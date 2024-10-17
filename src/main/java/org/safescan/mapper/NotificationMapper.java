package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ChatDTO;
import org.safescan.DTO.ResponseNotificationDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NotificationMapper {
    List<ResponseNotificationDTO> getLatestNotification(Integer userId, LocalDateTime time);

    void readNotification(Integer userId, String state);

    List<String> getLatestChat(Integer userId);

    void addNewChat(ChatDTO chat);
}
