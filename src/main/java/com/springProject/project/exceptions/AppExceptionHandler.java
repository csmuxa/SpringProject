package com.springProject.project.exceptions;

import com.springProject.project.iu.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = UserServiceException.class)
    public ResponseEntity<Object> handleUseraexception(UserServiceException ex, WebRequest request){
        ErrorMessage message=new ErrorMessage(new Date(),ex.getMessage());
        return new ResponseEntity<>(message,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleUseraexception(Exception ex, WebRequest request){
        ErrorMessage message=new ErrorMessage(new Date(),ex.getMessage());
        return new ResponseEntity<>(message,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
