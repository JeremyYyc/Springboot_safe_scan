package org.safescan.service;

import org.safescan.DTO.ResponseReportDTO;

public interface CameraService {
    public String callPythonService(String videoFilePath);

    ResponseReportDTO handleResponse(String pythonServiceResponse);
}
