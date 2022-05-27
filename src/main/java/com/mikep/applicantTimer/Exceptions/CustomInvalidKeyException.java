package com.mikep.applicantTimer.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomInvalidKeyException extends RuntimeException {
    public CustomInvalidKeyException(String message) { super(message); }
}
