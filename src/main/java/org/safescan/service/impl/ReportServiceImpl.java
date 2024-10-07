package org.safescan.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.safescan.DTO.ReportDTO;
import org.safescan.DTO.ResponseReportContentDTO;
import org.safescan.mapper.ReportMapper;
import org.safescan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapper reportMapper;

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
                    throw new RuntimeException("Failed to parse response from database");
                }
            }
            report.setContent(reportContent);
        }

        return reportLists;
    }
}