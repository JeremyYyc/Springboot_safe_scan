package org.safescan.controller;

import jakarta.validation.constraints.NotNull;
import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.Result;
import org.safescan.service.CameraService;
import org.safescan.service.ReportService;
import org.safescan.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @DeleteMapping("/delete")
    public Result<Object> deleteReports(@RequestParam Integer reportId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        reportService.deleteReports(userId, reportId);
        return Result.success("Successfully delete this report!", null);
    }


    @PutMapping("/update")
    public Result<ReportDTO> updateReports(@RequestBody @Validated(ReportDTO.Update.class) ReportDTO report) {
        Map<String, Object> map = ThreadLocalUtil.get();
        int userId = (Integer) map.get("userId");

        ReportDTO updatedReport = reportService.updateReports(
                userId,
                report.getReportId(),
                report.getReportName(),
                report.getAddressName()
        );
        return Result.success("Successfully update this report!", updatedReport);
    }
}
