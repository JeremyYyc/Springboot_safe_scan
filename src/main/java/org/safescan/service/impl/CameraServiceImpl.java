package org.safescan.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.safescan.DTO.ResponseReportDTO;
import org.safescan.mapper.CameraMapper;
import org.safescan.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import java.io.IOException;

@Service
public class CameraServiceImpl implements CameraService {
    @Autowired
    private CameraMapper cameraMapper;

    public String callPythonService(String videoFilePath){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .callTimeout(5, TimeUnit.MINUTES)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("video_path", videoFilePath)
                .build();

        Request request = new Request.Builder()
                .url("http://127.0.0.1:5001/processVideo")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // System.out.println("Y");
                // return response.body().string();
                String responseBody = response.body().string();
                System.out.println("Python service response: " + responseBody);  // 打印原始响应
                return responseBody;
            } else {
                System.out.println("Failed to call Python service");
                return "Error in calling Python service: " + response.message();
            }
        } catch (IOException e) {
            return "Exception in calling Python service: " + e.getMessage();
        }
    }

    @Override
    public ResponseReportDTO handleResponse(String pythonServiceResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResponseReportDTO reportDTO = objectMapper.readValue(pythonServiceResponse, ResponseReportDTO.class);
            // TODO: Store data into database by Mapper
            // cameraMapper.addReport(reportDTO);
            return reportDTO;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Python service response", e);
        }
    }
}
