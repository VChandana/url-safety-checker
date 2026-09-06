package com.chandana.urlsafetychecker.dto;

import com.chandana.urlsafetychecker.model.Classification;
import com.chandana.urlsafetychecker.model.RiskSignal;

import java.util.List;

public class ScanResponse {

    private String url;
    private Classification classification;
    private int riskScore;
    private List<RiskSignal> signals;

    public ScanResponse(String url, Classification classification, int riskScore, List<RiskSignal> signals) {
        this.url = url;
        this.classification = classification;
        this.riskScore = riskScore;
        this.signals = signals;
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

    public List<RiskSignal> getSignals() {
        return signals;
    }
}
