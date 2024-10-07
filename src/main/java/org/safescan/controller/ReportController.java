package org.safescan.controller;

import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.Result;
import org.safescan.service.CameraService;
import org.safescan.service.ReportService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@Validated
public class ReportController {
    @Autowired
    ReportService reportService;
    @Autowired
    CameraService cameraService;

    @GetMapping("/get")
    public Result<List<ReportDTO>> getReports() {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        List<ReportDTO> reportsList = reportService.getReports(userId);

        // Generate url for key frames
        for (int i = 0; i < reportsList.size(); i++) {
            ReportDTO report = reportsList.get(i);
            List<String> images = report.getContent().getRepresentativeImages();
            if (images != null) {
                report = cameraService.generateUrl(images, report);
                reportsList.set(i, report);
            }
        }

        return Result.success("Successfully get report by user: " + userId, reportsList);
    }
}
