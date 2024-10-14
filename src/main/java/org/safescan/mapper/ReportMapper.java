package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ReportDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {
    List<ReportDTO> getReports(Integer userId);

    String getContent(Integer reportId);

    ReportDTO getReportByReportId(Integer reportId);

    void deleteReport(Integer reportId, Integer userId);

    void updateByReportId(Integer reportId, String reportName, String addressName, LocalDateTime updateTime);
}
