package com.chandana.urlsafetychecker.service;

import com.chandana.urlsafetychecker.client.UrlRedirectClient;
import com.chandana.urlsafetychecker.dto.ScanResponse;
import com.chandana.urlsafetychecker.model.BlackListType;
import com.chandana.urlsafetychecker.model.Classification;
import com.chandana.urlsafetychecker.model.RedirectResult;
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

    @Autowired
    private UrlRedirectClient urlRedirectClient;

    private Classification classifyRisk(int riskScore){
        if(riskScore>=50) return Classification.MALICIOUS;

        if(riskScore>=20) return Classification.SUSPICIOUS;

        return Classification.SAFE;
    }

    public ScanResponse createBlackListResponse(String url,String name){
        RiskSignal signal = new RiskSignal(name,100);
        return new ScanResponse(url,null, Classification.MALICIOUS,100,List.of(signal));
    }

    public ScanResponse scan(String url){

        if(!UrlUtils.isValidHttpUrl(url)){
            return new ScanResponse(url, null, Classification.SUSPICIOUS,50,List.of());
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

        RedirectResult redirectResult = urlRedirectClient.resolveFinalUrl(url);
        URI finalUri = redirectResult.getFinalUrl();

        if(redirectResult.isSuccess() && !finalUri.equals(uri)){
            boolean finalBlacklistedUrl = blackListService.isBlackListed(finalUri.toString(),BlackListType.URL);
            String finalDomain = finalUri.getHost().toLowerCase();
            boolean finalBlacklistedDomain = blackListService.isBlackListed(finalDomain,BlackListType.DOMAIN);

            if(finalBlacklistedUrl){
                return createBlackListResponse(url,"Final URL found in blacklist");
            }

            if(finalBlacklistedDomain){
                return createBlackListResponse(url,"Final Domain found in blacklist");
            }
        }

        List<RiskSignal> signals = riskAnalyzer.calculateRisk(uri);

        if(redirectResult.isSuccess() && !finalUri.equals(uri)){
            signals.addAll(riskAnalyzer.calculateRisk(finalUri));
        }

        if(!redirectResult.isSuccess()){
            signals.add(new RiskSignal("Unable to resolve URL",20));
        }

        int riskScore = signals.stream()
                .mapToInt(RiskSignal::getScore)
                .sum();

        Classification classification = classifyRisk(riskScore);

        if(!redirectResult.isSuccess() && classification==Classification.SAFE){
            classification = Classification.SUSPICIOUS;
        }

        return new ScanResponse(url, redirectResult.isSuccess() ? finalUri.toString() : null,
                classification,riskScore,signals);
    }
}
