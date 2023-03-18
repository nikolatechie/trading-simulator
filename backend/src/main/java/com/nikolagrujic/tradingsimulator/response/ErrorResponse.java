package com.nikolagrujic.tradingsimulator.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    public String errorMessage;
}
