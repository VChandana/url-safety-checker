package com.chandana.urlsafetychecker.util;

import java.net.URI;

public class UrlUtils {

    public static URI parse(String url){
        return URI.create(url);
    }

    public static boolean isValidHttpUrl(String url){
        try{
            URI uri = URI.create(url);

            return ("http".equalsIgnoreCase(uri.getScheme()) ||
                    "https".equalsIgnoreCase(uri.getScheme()) &&
                    uri.getHost() != null);
        }catch(IllegalArgumentException e){
            return false;
        }
    }
}
