package com.chandana.urlsafetychecker.dto;

import jakarta.validation.constraints.NotBlank;

public class ScanRequest {

    @NotBlank(message = "Url should not be blank")
    private String url;

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
