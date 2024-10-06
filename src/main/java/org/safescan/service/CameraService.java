package org.safescan.service;

import org.safescan.DTO.ReportDTO;

import java.util.List;

public interface CameraService {
    String callPythonService(String videoFilePath, ReportDTO report);

    ReportDTO handleResponse(String pythonServiceResponse, ReportDTO report);

    ReportDTO handleMetaData(ReportDTO reportMetadata);

    ReportDTO generateUrl(List<String> representativeImages, ReportDTO report);
}

