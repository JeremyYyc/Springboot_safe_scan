package org.safescan.service;

import org.safescan.DTO.ChatDTO;
import org.safescan.DTO.ResponseNotificationDTO;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Map.Entry<String, List<ResponseNotificationDTO>> getNotification(Integer userId);

    void readNotification(Integer userId, String state);

    ChatDTO chatWithBot(ChatDTO question, Integer userId);
}
