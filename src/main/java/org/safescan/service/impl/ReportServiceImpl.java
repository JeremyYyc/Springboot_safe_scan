package org.safescan.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.ResponseReportContentDTO;
import org.safescan.mapper.ReportMapper;
import org.safescan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapper reportMapper;

    @Value("${app.uploadVideos.dir}")
    private String videoUploadDir;

    @Value("${app.generatedKeyFrame.dir}")
    private String keyFramesDir;

    @Override
    public List<ReportDTO> getReports(Integer userId) {
        List<ReportDTO> reportLists = reportMapper.getReports(userId);

        ObjectMapper objectMapper = new ObjectMapper();
        for (ReportDTO report : reportLists) {
            ResponseReportContentDTO reportContent = new ResponseReportContentDTO();
            String contentJson = reportMapper.getContent(report.getReportId());
            if (contentJson != null) {
                try {
                    reportContent = objectMapper.readValue(contentJson, ResponseReportContentDTO.class);
                } catch (IOException e) {
                    continue;
                }
            }
            report.setContent(reportContent);
        }

        return reportLists;
    }

    @Override
    public void deleteReports(Integer userId, Integer reportId) {
        boolean isFileDeleted = false;
        ReportDTO report = reportMapper.getReportByReportId(reportId);

        if (report == null) {
            throw new RuntimeException("Delete failed! No such a report record in report id!" + reportId);
        } else if (!Objects.equals(report.getUserId(), userId)) {
            throw new RuntimeException("You are not allowed to delete other user's report!");
        }

        try {
            String filePath = getFilePathFromUrl(report.getVideoUrl());

            if (filePath != null) {
                File file = new File(filePath);
                isFileDeleted = file.delete();
            }

            // Delete key frames
            // Generate file names of key frames
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseReportContentDTO reportContent;
            String contentJson = reportMapper.getContent(reportId);
            if (contentJson != null) {
                try {
                    reportContent = objectMapper.readValue(contentJson, ResponseReportContentDTO.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to parse response from database!");
                }

                List<String> keyFrames = reportContent.getRepresentativeImages();
                if (keyFrames != null) {
                    String filename = keyFramesDir + keyFrames.get(0).replace("\\", "/")
                            .replace("./", "/");
                    Path path = Paths.get(filename);
                    File toDelete = new File(path.getParent().toString());

                    File[] allContents = toDelete.listFiles();
                    if (allContents != null) {
                        for (File file : allContents) {
                            if (!file.delete()) {
                                throw new RuntimeException("Error in deleting this file: " + file);
                            }
                        }
                    }
                    if (!toDelete.delete()) {
                        throw new RuntimeException("Error in deleting this file: " + toDelete);
                    }
                }
            }

            if (isFileDeleted) {
                reportMapper.deleteReport(reportId, userId);
            } else {
                throw new RuntimeException("Error to delete files from the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ReportDTO updateReports(Integer userId, Integer reportId, String reportName, String addressName) {
        ReportDTO report = reportMapper.getReportByReportId(reportId);
        if (!Objects.equals(userId, report.getUserId())) {
            throw new RuntimeException("You are not allow to change other users' report!");
        } else if (Objects.equals(reportName, report.getReportName())
                && Objects.equals(addressName, report.getAddressName())) {
            throw new RuntimeException("Please make changes first!");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseReportContentDTO reportContent = null;
        String contentJson = reportMapper.getContent(report.getReportId());
        if (contentJson != null) {
            try {
                reportContent = objectMapper.readValue(contentJson, ResponseReportContentDTO.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse response from database!");
            }
        }
        reportMapper.updateByReportId(reportId, reportName, addressName, LocalDateTime.now());
        ReportDTO updatedReport = reportMapper.getReportByReportId(reportId);
        updatedReport.setContent(reportContent);
        return updatedReport;
    }


    public String getFilePathFromUrl(String fileUrl) {
        // Extract the relative path portion from a URL
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/uploads/") + "/uploads/".length());

        // Concatenate the file storage root directory and file name to get the full path of the file on the server
        return Paths.get(videoUploadDir, fileName).toString();
    }
}