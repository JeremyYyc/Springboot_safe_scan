package org.safescan.controller;

import org.safescan.DTO.ChatDTO;
import org.safescan.DTO.ResponseNotificationDTO;
import org.safescan.DTO.Result;
import org.safescan.service.NotificationService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@Validated
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping
    public Result<List<ResponseNotificationDTO>> getNotification() {
        Map<String,Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        Map.Entry<String, List<ResponseNotificationDTO>> notification = notificationService.getNotification(userId);

        if (notification == null) {
            return Result.error("No notification found!");
        }

        return Result.success(notification.getKey(), notification.getValue());
    }


    @PostMapping
    public Result<Object> readNotification(@RequestParam String state) {
        Map<String,Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        notificationService.readNotification(userId, state);
        return Result.success();
    }


    @PutMapping
    public Result<ChatDTO> chatBot(@RequestBody ChatDTO question) {
        Map<String,Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        ChatDTO chat = notificationService.chatWithBot(question, userId);
        return Result.success("Successfully get reply!", chat);
    }
}
