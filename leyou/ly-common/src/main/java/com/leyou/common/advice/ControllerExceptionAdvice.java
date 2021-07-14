package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//相当于给对应的controller加了try
@ControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {


    @ExceptionHandler
    public ResponseEntity<String> handleException(LyException ex) {

        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity.status(500).body(ex.getMessage());
    }
}
