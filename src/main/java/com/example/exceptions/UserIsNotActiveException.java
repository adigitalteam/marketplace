package com.example.exceptions;

public class UserIsNotActiveException extends AppException{

    public UserIsNotActiveException(String message) {
        super(message);
    }

}
