package com.chandana.urlsafetychecker.model;

import java.net.URI;

public class RedirectResult {
    private final URI finalUrl;

    private final boolean success;

    public RedirectResult(URI finalUrl, boolean success){
        this.finalUrl = finalUrl;
        this.success = success;
    }

    public URI getFinalUrl(){
        return finalUrl;
    }

    public boolean isSuccess(){
        return success;
    }
}
