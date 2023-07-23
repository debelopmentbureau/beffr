package com.fedex.beffr.controller;

import com.fedex.beffr.exception.BadRequestException;
import com.fedex.beffr.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ResponseError> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity<>(ResponseError.builder().message(exception.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    protected ResponseEntity<ResponseError> handleServerException(ServerException exception) {
        return new ResponseEntity<>(ResponseError.builder().message(exception.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
