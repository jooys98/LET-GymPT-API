package com.example.gympt.exception;

public class CustomDoesntExist extends RuntimeException {
    public CustomDoesntExist(String message) {
        super(message);
    }
}
