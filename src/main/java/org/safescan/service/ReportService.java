package org.safescan.service;

import org.safescan.DTO.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReports(Integer userId);

    void deleteReports(Integer userId, Integer reportId);

    ReportDTO updateReports(Integer userId, Integer reportId, String reportName, String addressName);
}
