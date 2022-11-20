package com.fedex.beffr.controller;

import com.fedex.beffr.exception.BadRequestException;
import com.fedex.beffr.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ControllerAdviceTest {
    ControllerAdvice controllerAdviceUnderTest = new ControllerAdvice();

    @Test
    void handleBadRequestException() {
        final ResponseEntity<ResponseError> badRequestException = controllerAdviceUnderTest.handleBadRequestException(new BadRequestException("BadRequestException"));
        assertEquals(badRequestException.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(badRequestException.getBody().getMessage(), "BadRequestException");
    }

    @Test
    void handleServerException() {
        final ResponseEntity<ResponseError> badRequestException = controllerAdviceUnderTest.handleServerException(new ServerException("ServerException"));
        assertEquals(badRequestException.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(badRequestException.getBody().getMessage(), "ServerException");
    }
}