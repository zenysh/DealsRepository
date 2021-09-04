package com.data.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.BAD_REQUEST)
public class InvalidException extends RuntimeException{
    public InvalidException(String message) {
        super(message);
    }
}
