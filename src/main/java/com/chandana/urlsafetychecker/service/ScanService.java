package com.chandana.urlsafetychecker.service;

import com.chandana.urlsafetychecker.dto.ScanResponse;
import com.chandana.urlsafetychecker.model.Classification;
import com.chandana.urlsafetychecker.util.UrlUtils;
import org.springframework.stereotype.Service;

@Service
public class ScanService {

    public ScanResponse scan(String url){

        if(!UrlUtils.isValidHttpUrl(url)){
            return new ScanResponse(url, Classification.SUSPICIOUS,50);
        }
        return new ScanResponse(url, Classification.SAFE,0);
    }
}
