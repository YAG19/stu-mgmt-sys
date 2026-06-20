package com.student.mgmtsys.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionalHandler {


    @ExceptionHandler(NoStudentFoundException.class)
    public ResponseEntity<String> hanlder(String message, Exception exception){
//        return (ResponseEntity<String>) ResponseEntity.notFound(" SAdasd");
        return null;
    }

}
