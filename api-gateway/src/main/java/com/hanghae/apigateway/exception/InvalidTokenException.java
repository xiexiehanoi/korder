package com.hanghae.apigateway.exception;

public class InvalidTokenException extends  RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
