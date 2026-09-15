package com.chandana.urlsafetychecker.controller;

import com.chandana.urlsafetychecker.client.UrlRedirectClient;
import com.chandana.urlsafetychecker.dto.ScanRequest;
import com.chandana.urlsafetychecker.dto.ScanResponse;
import com.chandana.urlsafetychecker.service.ScanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScanController {

    @Autowired
    private ScanService scanService;

    @Autowired
    private UrlRedirectClient urlRedirectClient;

    @PostMapping("api/v1/scan")
    public ScanResponse scan(@Valid @RequestBody ScanRequest request){
        return scanService.scan(request.getUrl());
    }

    @GetMapping("/test-redirect")
    public String testRedirect(@RequestParam String url){
        return urlRedirectClient.resolveFinalUrl(url).toString();
    }
}
