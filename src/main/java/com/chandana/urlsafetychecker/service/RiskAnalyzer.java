package com.chandana.urlsafetychecker.service;

import com.chandana.urlsafetychecker.model.RiskSignal;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskAnalyzer {

    private static final int LONG_URL_THRESHOLD = 200;
    private static final int HTTP_PENALTY = 10;
    private static final int IP_ADDRESS_PENALTY = 20;
    private static final int SUSPICIOUS_KEYWORD_PENALTY = 15;
    private static final int LONG_URL_PENALTY = 10;
    private static final int SUSPICIOUS_TLD_PENALTY = 15;

    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "login",
            "verify",
            "account",
            "password",
            "signin"
    );

    private static final List<String> SUSPICIOUS_TLDS = List.of(
            ".xyz",
            ".top",
            ".click",
            ".zip"
    );

    // heuristic risk signal 1 - http instead of https
    public List<RiskSignal> calculateRisk(URI uri){
        List<RiskSignal> signals = new ArrayList<>();

        if("http".equalsIgnoreCase(uri.getScheme())){
            signals.add(new RiskSignal("HTTP connection", HTTP_PENALTY));
        }

        if(isIpAddress(uri.getHost())){
            signals.add(new RiskSignal("IP address used as host", IP_ADDRESS_PENALTY));
        }

        if(containsSuspiciousKeywords(uri)){
            signals.add(new RiskSignal("Suspicious keyword detected", SUSPICIOUS_KEYWORD_PENALTY));
        }

        if(isLongUrl(uri)){
            signals.add(new RiskSignal("Unusually long URL", LONG_URL_PENALTY));
        }

        if(hasSuspiciousTld(uri.getHost())){
            signals.add(new RiskSignal("Suspicious TLD", SUSPICIOUS_TLD_PENALTY));
        }

        if(containsIpLikePattern(uri.getHost())){
            signals.add(new RiskSignal("IP-like pattern in hostname",IP_ADDRESS_PENALTY));
        }

        return signals;
    }

    // heuristic risk signal 2 - ipaddress in url
    public boolean isIpAddress(String host){
        return host.matches("\\d{1,3}(\\.\\d{1,3}){3}"); //regex to recognise IPv4 address
    }

    //suspicious keyword detection
    public boolean containsSuspiciousKeywords(URI uri){
        String url = uri.toString().toLowerCase();

        return SUSPICIOUS_KEYWORDS.stream().anyMatch(url::contains);
    }

    //long url detection
    public boolean isLongUrl(URI uri){
        return uri.toString().length() > LONG_URL_THRESHOLD;
    }

    //suspicious TLD Detection
    public boolean hasSuspiciousTld(String host){
//        String host = uri.getHost();

        if(host==null) return false;

        String lowercaseHost = host.toLowerCase();
        return SUSPICIOUS_TLDS.stream().anyMatch(lowercaseHost::endsWith);
    }

    //IP-like hostname detection
    public boolean containsIpLikePattern(String host){
        if(host == null) return false;

        return host.matches("\\d{1,3}(\\.\\d{1,3}){3}\\..+");
    }

}
