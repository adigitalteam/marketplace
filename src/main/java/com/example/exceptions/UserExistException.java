package com.example.exceptions;

public class UserExistException extends AppException {
    public UserExistException(String message){
        super(message);
    }
}
