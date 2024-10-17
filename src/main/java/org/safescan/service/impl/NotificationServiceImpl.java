package org.safescan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.safescan.DTO.ChatDTO;
import org.safescan.DTO.ResponseNotificationDTO;
import org.safescan.mapper.NotificationMapper;
import org.safescan.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    NotificationMapper notificationMapper;

    @Override
    public Map.Entry<String, List<ResponseNotificationDTO>> getNotification(Integer userId) {
        LocalDateTime time = LocalDateTime.now();
        List<ResponseNotificationDTO> responseNotification = notificationMapper.getLatestNotification(userId, time);

        String message = "Successfully get all notifications!";

        return new AbstractMap.SimpleEntry<>(message, responseNotification);
    }

    @Override
    public void readNotification(Integer userId, String state) {
        notificationMapper.readNotification(userId, state);
    }

    @Override
    public ChatDTO chatWithBot(ChatDTO chat, Integer userId) {
        // Get the latest chat questions and add the current question to the list
        List<String> questionList = notificationMapper.getLatestChat(userId);
        questionList.add(chat.getQuestion());

        // Convert the questions to JSON format
        ObjectMapper objectMapper = new ObjectMapper();
        String questionListJson;
        try {
            // Create a map to hold the "questions" key with the list of questions
            Map<String, List<String>> questionsMap = new HashMap<>();
            questionsMap.put("questions", questionList);
            questionListJson = objectMapper.writeValueAsString(questionsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate Json Object!" + e.getMessage());
        }

        // Set up OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .callTimeout(5, TimeUnit.MINUTES)
                .build();

        // Create the request body using MediaType JSON
        RequestBody formBody = new FormBody.Builder()
                .add("user_input", questionListJson)
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5002/processChat")
                .post(formBody)
                .build();

        // Send the request and get the response
        String reply;
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                reply = response.body() != null ? response.body().string() : null;
            } else {
                throw new RuntimeException("Error in calling Python service: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception in calling Python service: " + e.getMessage());
        }

        // Set the reply and other properties, then save the chat
        chat.setReply(reply);
        chat.setUserId(userId);
        chat.setCreateTime(LocalDateTime.now());
        notificationMapper.addNewChat(chat);
        return chat;
    }

}
