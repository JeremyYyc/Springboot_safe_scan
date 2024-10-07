package org.safescan.service;

import org.safescan.DTO.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReports(Integer userId);
}
