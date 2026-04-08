package com.sebastian.dev.usermanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User not found")
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String msg){
        super(msg);
    }
}
