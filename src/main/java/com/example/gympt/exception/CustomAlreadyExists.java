package com.example.gympt.exception;

public class CustomAlreadyExists extends RuntimeException {
    public CustomAlreadyExists(String message) {
        super(message);
    }
}
