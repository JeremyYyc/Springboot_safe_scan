package org.safescan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.ResponseReportContentDTO;
import org.safescan.mapper.CameraMapper;
import org.safescan.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.io.IOException;

@Service
public class CameraServiceImpl implements CameraService {
    @Autowired
    private CameraMapper cameraMapper;

    public String callPythonService(String videoFilePath, ReportDTO report){
        ObjectMapper objectMapper = new ObjectMapper();
        String attributesJson;
        try {
            attributesJson = objectMapper.writeValueAsString(report.getAttributes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate Json Object!" + e.getMessage());
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .callTimeout(5, TimeUnit.MINUTES)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("video_path", videoFilePath)
                .add("attributes", attributesJson)
                .build();

        Request request = new Request.Builder()
                .url("http://127.0.0.1:5001/processVideo")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                throw new RuntimeException("Error in calling Python service: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception in calling Python service: " + e.getMessage());
        }
    }

    @Override
    public ReportDTO handleResponse(String pythonServiceResponse, ReportDTO report) {
        LocalDateTime time = LocalDateTime.now();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResponseReportContentDTO reportContent = objectMapper.
                    readValue(pythonServiceResponse, ResponseReportContentDTO.class);

            // Store data into database by Mapper
            report.setContent(reportContent);
            report.setUpdateTime(time);
            cameraMapper.addReport(report.getReportId(), pythonServiceResponse, time);
            return report;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Python service response", e);
        }
    }

    @Override
    public ReportDTO handleMetaData(ReportDTO reportMetadata) {
        LocalDateTime time = LocalDateTime.now();
        reportMetadata.setCreateTime(time);
        reportMetadata.setUpdateTime(time);
        cameraMapper.addMetaData(reportMetadata);
        return reportMetadata;
    }

    @Override
    public ReportDTO generateUrl(List<String> representativeImages, ReportDTO report) {
        List<String> representativeImagesUrl = new ArrayList<>();

        // Generate video store url
        for (String image : representativeImages) {
            String imagePath = image.replace("\\", "/").replace("./", "");

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/generated/")
                    .path(imagePath)
                    .toUriString();
            representativeImagesUrl.add(fileUrl);
        }

        report.getContent().setRepresentativeImages(representativeImagesUrl);
        return report;

    }
}
