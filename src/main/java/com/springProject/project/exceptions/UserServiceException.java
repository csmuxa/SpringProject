package com.springProject.project.exceptions;

public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}
