package com.example.exceptions;

public class UserIsActiveException extends AppException {
    public UserIsActiveException(String message){
        super(message);
    }
}
