package unileste.homefinance.exceptions.handlers;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.exceptions.AuthException;
import unileste.homefinance.exceptions.HouseException;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorResponse> handleGenericException(Exception e) {
        log.error("handleGenericException - {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }
    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<DefaultErrorResponse> handleIllegalAccessError(IllegalAccessError e) {
        log.info("handleIllegalAccessError - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<DefaultErrorResponse> handleAuthException(AuthException e) {
        log.info("handleAuthException - {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<DefaultErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<DefaultErrorResponse> handleFeignException(FeignException e) {
        log.info("handleFeignException - {}", e.getMessage(), e);
        return ResponseEntity.status(e.status()).body(DefaultErrorResponse.builder().message(e.getMessage()).build());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info("handleIllegalArgumentException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(HouseException.class)
    public ResponseEntity<DefaultErrorResponse> handleHouseException(HouseException ex) {
        log.info("handleHouseException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.info("handleHttpRequestMethodNotSupportedException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(DefaultErrorResponse.builder().message(ex.getMessage()).build());
    }
}