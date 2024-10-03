package org.safescan.controller;

import org.safescan.DTO.Result;
import org.safescan.exception.FilePathException;
import org.safescan.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/camera")
@Validated
public class CameraController {
    @Autowired
    CameraService cameraService;

    @Value("${app.uploadVideos.dir}")
    private String videoUploadDir;

    @PostMapping("/upload")
    public Result<String> uploadVideo(@RequestParam("video") MultipartFile videoFile) {
        System.out.println("Received file: " + videoFile.getOriginalFilename());
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
            System.out.println("File saved to: " + filePath);

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            String pythonServiceResponse = cameraService.callPythonService(filePath.toString());
            System.out.println(pythonServiceResponse);

            return Result.success("Video uploaded successfully!", fileUrl);
        } catch (IOException e) {
            return Result.error("Failed to upload video!");
        }
    }
}
