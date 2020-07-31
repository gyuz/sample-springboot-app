package com.programming.customer.controller;

import com.programming.customer.dto.ErrorDTO;
import com.programming.customer.exception.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@ControllerAdvice(assignableTypes =  CustomerController.class)
public class CustomerControllerAdvice {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorDTO> customerNotFoundException(CustomerNotFoundException customerNotFoundException, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), customerNotFoundException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), methodArgumentNotValidException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException constraintViolationException, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), constraintViolationException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> globleExcpetionHandler(Exception exception, WebRequest webRequest) {
        ErrorDTO errorDTO = new ErrorDTO(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
