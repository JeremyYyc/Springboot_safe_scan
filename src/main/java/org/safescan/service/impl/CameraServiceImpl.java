package org.safescan.service.impl;

import okhttp3.*;
import org.safescan.service.CameraService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CameraServiceImpl implements CameraService {
    public String callPythonService(String videoFilePath){
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("video_path", videoFilePath)
                .build();

        Request request = new Request.Builder()
                .url("http://172.20.10.2:5001/processVideo")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Y");
                return response.body().string();
            } else {
                System.out.println("N");
                return "Error in calling Python service: " + response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception in calling Python service: " + e.getMessage();
        }
    }
}
