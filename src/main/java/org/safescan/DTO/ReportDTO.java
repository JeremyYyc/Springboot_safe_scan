package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReportDTO {
    private Integer reportId;
    private Integer userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String videoUrl;
    private ResponseReportContentDTO content;
    private AttributesDTO attributes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
