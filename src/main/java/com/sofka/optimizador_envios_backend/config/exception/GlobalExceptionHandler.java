package com.sofka.optimizador_envios_backend.config.exception;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed", errors));
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoInvalido(PedidoInvalidoException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), List.of()));
    }

    public record ErrorResponse(String message, List<String> errors) {}
}
