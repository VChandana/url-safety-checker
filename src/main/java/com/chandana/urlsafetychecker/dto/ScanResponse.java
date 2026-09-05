package com.chandana.urlsafetychecker.dto;

import com.chandana.urlsafetychecker.model.Classification;

public class ScanResponse {

    private String url;
    private Classification classification;
    private int riskScore;

    public ScanResponse(String url, Classification classification, int riskScore) {
        this.url = url;
        this.classification = classification;
        this.riskScore = riskScore;
    }

    public String getUrl() {
        return url;
    }

    public Classification getClassification() {
        return classification;
    }

    public int getRiskScore() {
        return riskScore;
    }
}
