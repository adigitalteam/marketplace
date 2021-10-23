package com.example.config;

import com.example.dto.ApplicationResponse;
import com.example.dto.ErrorDTO;
import com.example.exceptions.AppException;
import com.example.exceptions.UserExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ApplicationResponse> handleConflict(RuntimeException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = {AppException.class})
    protected ResponseEntity<ApplicationResponse> handleConflict(AppException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = {UserExistException.class})
    protected ResponseEntity<ApplicationResponse> UserExistException(UserExistException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.GONE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        //Get all errors
        List<ErrorDTO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> (new ErrorDTO(x.getField(),x.getDefaultMessage())))
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);

    }
}
