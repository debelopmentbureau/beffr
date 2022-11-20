package com.fedex.beffr.controller;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseError {
    private String message;
}
