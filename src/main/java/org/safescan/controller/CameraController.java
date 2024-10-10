package org.safescan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.Result;
import org.safescan.exception.FilePathException;
import org.safescan.service.CameraService;
import org.safescan.service.UserService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/camera")
@Validated
public class CameraController {
    @Autowired
    CameraService cameraService;
    @Autowired
    UserService userService;

    @Value("${app.uploadVideos.dir}")
    private String videoUploadDir;

    @PostMapping("/upload")
    public Result<ReportDTO> uploadVideo(
            @RequestPart("video") MultipartFile videoFile,
            @RequestParam("report") String reportMetadataJson) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        if (videoFile.isEmpty()) {
            return Result.error("No file uploaded.");
        }

        File uploadDir = new File(videoUploadDir);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new FilePathException("Failed to create path for videos!");
            }
        }

        String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
        Path filePath = Paths.get(videoUploadDir, fileName);

        try {
            videoFile.transferTo(filePath);

            // Generate video store url
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            ObjectMapper objectMapper = new ObjectMapper();
            ReportDTO reportMetadata = new ReportDTO();
            if (reportMetadataJson == null || reportMetadataJson.trim().isEmpty()) {
                reportMetadata.setUserId(userId);
            } else {
                try {
                    reportMetadata = objectMapper.readValue(reportMetadataJson, ReportDTO.class);
                } catch (JsonProcessingException e) {
                    return Result.error(e.getMessage());
                }
            }


            if (reportMetadata.getAttributes() == null) {
                reportMetadata.setAttributes(userService.getAttributes(userId));
            } else {
                userService.setAttributes(userId, reportMetadata.getAttributes());
            }

            // Store user id and url value into report object and store it into database
            reportMetadata.setUserId(userId);
            reportMetadata.setVideoUrl(fileUrl);
            ReportDTO report = cameraService.handleMetaData(reportMetadata);

            // Execute python script to generate report by JSON data type
            String responseBody = cameraService.callPythonService(filePath.toString(), report);

            // Store report data into database
            ReportDTO responseReport = cameraService.handleResponse(responseBody, report);

            // Generate url for key frames
            responseReport = cameraService.
                    generateUrl(responseReport.getContent().getRepresentativeImages(), responseReport);

            return Result.success("Video uploaded successfully!", responseReport);
        } catch (IOException e) {
            return Result.error("Failed to upload video!");
        }
    }
}
