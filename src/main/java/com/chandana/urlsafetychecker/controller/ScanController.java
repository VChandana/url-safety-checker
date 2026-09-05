package com.chandana.urlsafetychecker.controller;

import com.chandana.urlsafetychecker.dto.ScanRequest;
import com.chandana.urlsafetychecker.dto.ScanResponse;
import com.chandana.urlsafetychecker.service.ScanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScanController {

    @Autowired
    private ScanService scanService;

    @PostMapping("api/v1/scan")
    public ScanResponse scan(@Valid @RequestBody ScanRequest request){
        return scanService.scan(request.getUrl());
    }
}
