package org.safescan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.safescan.DTO.ReportDTO;

import java.time.LocalDateTime;

@Mapper
public interface CameraMapper {
    void addReport(Integer reportId, String content, LocalDateTime updateTime);

    void addMetaData(ReportDTO reportMetadata);
}
