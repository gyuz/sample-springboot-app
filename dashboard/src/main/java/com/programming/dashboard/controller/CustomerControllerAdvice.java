package com.programming.dashboard.controller;

import com.programming.dashboard.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice(assignableTypes =  CustomerDashboardController.class)
public class CustomerControllerAdvice {

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorDTO> handleHttpServerErrorException(HttpServerErrorException httpServerErrorException, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), httpServerErrorException.getMessage(), webRequest.getDescription(true));
        return new ResponseEntity<>(errorDTO, httpServerErrorException.getStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDTO> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), httpClientErrorException.getMessage(), webRequest.getDescription(true));
        return new ResponseEntity<>(errorDTO, httpClientErrorException.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> globleExcpetionHandler(Exception exception, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), exception.getMessage(), webRequest.getDescription(true));
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

