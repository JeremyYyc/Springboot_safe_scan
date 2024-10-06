package org.safescan.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ResponseReportDTO {
    private int code;
    private String message;

    @JsonProperty("regionInfo")
    private List<RegionInfo> regionInfo;

    @JsonProperty("representativeImages")
    private List<String> representativeImages;

    @Data
    public static class RegionInfo {
        @JsonProperty("childSafetyHazards")
        private List<String> childSafetyHazards;

        @JsonProperty("colorAndLightingEvaluation")
        private List<String> colorAndLightingEvaluation;

        @JsonProperty("potentialHazards")
        private List<String> potentialHazards;

        @JsonProperty("regionName")
        private List<String> regionName;

        @JsonProperty("scores")
        private List<Double> scores;

        @JsonProperty("suggestions")
        private List<String> suggestions;
    }
}
