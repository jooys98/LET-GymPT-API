package com.example.gympt.exception;

public class NotAccessChatRoom extends RuntimeException {
    public NotAccessChatRoom(String message) {
        super(message);
    }
}
