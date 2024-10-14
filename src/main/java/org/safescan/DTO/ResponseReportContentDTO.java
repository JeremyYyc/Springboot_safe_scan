package org.safescan.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ResponseReportContentDTO {
    private List<RegionInfo> regionInfo;
    private List<String> representativeImages;

    @Data
    public static class RegionInfo {
        private List<String> specialHazards;
        private List<String> colorAndLightingEvaluation;
        private List<String> potentialHazards;
        private List<String> regionName;
        private List<Double> scores;
        private List<String> suggestions;
    }
}
