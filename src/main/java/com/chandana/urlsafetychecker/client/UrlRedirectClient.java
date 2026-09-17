package com.chandana.urlsafetychecker.client;

import com.chandana.urlsafetychecker.model.RedirectResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class UrlRedirectClient {

    private final HttpClient httpClient;

    public UrlRedirectClient(){
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public RedirectResult resolveFinalUrl(String url){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            int statusCode = response.statusCode();
            if(statusCode==405){
                //HEAD is not supported so use GET
                HttpRequest getRequest = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<Void> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.discarding());
                return new RedirectResult(getResponse.uri(),true);
            }

            return new RedirectResult(response.uri(),true);
        }catch (IOException | InterruptedException e){
            return new RedirectResult(URI.create(url),false);
        }
    }
}
