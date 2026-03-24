package unileste.homefinance.exceptions.handlers;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.exceptions.AuthException;
import unileste.homefinance.exceptions.ExpenseException;
import unileste.homefinance.exceptions.HouseException;
import unileste.homefinance.exceptions.HouseNotFoundException;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorResponse> handleGenericException(Exception e) {
        log.warn("handleGenericException - {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }
    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<DefaultErrorResponse> handleIllegalAccessError(IllegalAccessError e) {
        log.warn("handleIllegalAccessError - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<DefaultErrorResponse> handleAuthException(AuthException ex) {
        log.warn("handleAuthException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        log.warn("handleNotFound - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<DefaultErrorResponse> handleFeignException(FeignException ex) {
        log.warn("handleFeignException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.status()).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("handleIllegalArgumentException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(HouseException.class)
    public ResponseEntity<DefaultErrorResponse> handleHouseException(HouseException ex) {
        log.warn("handleHouseException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("handleHttpRequestMethodNotSupportedException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(HouseNotFoundException.class)
    public ResponseEntity<DefaultErrorResponse> handleHouseNotFoundException(HouseNotFoundException ex) {
        log.warn("handleHouseNotFoundException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<DefaultErrorResponse> handleMissingPathVariableException(MissingPathVariableException ex) {
        log.warn("handleMissingPathVariableException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(ExpenseException.class)
    public ResponseEntity<DefaultErrorResponse> handleExpenseException(ExpenseException ex) {
        log.warn("handleExpenseException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
}