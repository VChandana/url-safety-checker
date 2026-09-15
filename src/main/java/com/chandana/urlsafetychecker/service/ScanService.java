package com.chandana.urlsafetychecker.service;

import com.chandana.urlsafetychecker.dto.ScanResponse;
import com.chandana.urlsafetychecker.model.BlackListType;
import com.chandana.urlsafetychecker.model.Classification;
import com.chandana.urlsafetychecker.model.RiskSignal;
import com.chandana.urlsafetychecker.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class ScanService {

    @Autowired
    private RiskAnalyzer riskAnalyzer;

    @Autowired
    private BlackListService blackListService;

    private Classification classifyRisk(int riskScore){
        if(riskScore>=50) return Classification.MALICIOUS;

        if(riskScore>=20) return Classification.SUSPICIOUS;

        return Classification.SAFE;
    }

    public ScanResponse createBlackListResponse(String url,String name){
        RiskSignal signal = new RiskSignal(name,100);
        return new ScanResponse(url,Classification.MALICIOUS,100,List.of(signal));
    }

    public ScanResponse scan(String url){

        if(!UrlUtils.isValidHttpUrl(url)){
            return new ScanResponse(url, Classification.SUSPICIOUS,50,List.of());
        }

        URI uri = URI.create(url);

        boolean blacklistedURL = blackListService.isBlackListed(url, BlackListType.URL);

        String domain = uri.getHost().toLowerCase();
        boolean blacklistedDomain = blackListService.isBlackListed(domain,BlackListType.DOMAIN);

        if(blacklistedURL){
            return createBlackListResponse(url,"URL found in blacklist");
        }

        if(blacklistedDomain){
            return createBlackListResponse(url,"Domain found in blacklist");
        }

        List<RiskSignal> signals = riskAnalyzer.calculateRisk(uri);

        int riskScore = signals.stream()
                .mapToInt(RiskSignal::getScore)
                .sum();

        Classification classification = classifyRisk(riskScore);

        return new ScanResponse(url, classification,riskScore,signals);
    }
}
