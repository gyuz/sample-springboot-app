package com.programming.dashboard.handler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Component
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException{
        HttpStatus status = clientHttpResponse.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }

    @SneakyThrows
    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) {
        String responseAsString = clientHttpResponse.getStatusCode().toString();
        log.error("ResponseBody: {}", responseAsString);
        if (clientHttpResponse.getStatusCode().is4xxClientError()) {
            throw new HttpClientErrorException(clientHttpResponse.getStatusCode());
        }
        throw new HttpServerErrorException(clientHttpResponse.getStatusCode());
    }

    @SneakyThrows
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse clientHttpResponse) {
        log.error("URL: {}, HttpMethod: {}, ResponseBody: {}", url, method, clientHttpResponse.getStatusText());
        if (clientHttpResponse.getStatusCode().is4xxClientError()) {
            throw new HttpClientErrorException(clientHttpResponse.getStatusCode());
        }
        throw new HttpServerErrorException(clientHttpResponse.getStatusCode());
    }
}
