package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReportDTO {
    @NotNull(groups = ForumDTO.Update.class)
    private Integer reportId;
    private Integer userId;

    @NotNull(groups = ForumDTO.Update.class)
    private String reportName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(groups = ForumDTO.Update.class)
    private String addressName;
    private String videoUrl;
    private ResponseReportContentDTO content;
    private AttributesDTO attributes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public interface Update extends Default {}
}
