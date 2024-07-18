package edu.njust.homework_manager.handlers;

import edu.njust.homework_manager.protocol.ApiResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
@ControllerAdvice
public class ValidationFailedHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new LinkedList<>();

        ex.getBindingResult().getAllErrors().forEach( (error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        ApiResult<List<String>> apiResult = ApiResult.<List<String>>builder()
                .status(400)
                .timestamp(new Date())
                .error("Validation Failed")
                .data(errors)
                .build();

        return ResponseEntity.badRequest().body(apiResult);
    }
}
