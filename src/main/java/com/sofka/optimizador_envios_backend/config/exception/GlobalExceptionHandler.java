package com.sofka.optimizador_envios_backend.config.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.putIfAbsent(error.getField(), error.getDefaultMessage())
    );

    ex.getBindingResult().getGlobalErrors().forEach(error ->
        errors.putIfAbsent(error.getObjectName(), error.getDefaultMessage())
    );

    return ResponseEntity.badRequest().body(new ErrorResponse(
        "La solicitud contiene datos invalidos",
        errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .toList()
    ));
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handlePedidoInvalido(PedidoInvalidoException ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        try {
            if (cause instanceof InvalidFormatException invalidFormatException) {
                String fieldPath = buildFieldPath(invalidFormatException.getPath().stream()
                        .map(reference -> reference.getFieldName())
                        .toList());

                String message = resolveInvalidFormatMessage(fieldPath, invalidFormatException.getTargetType());

                return ResponseEntity.badRequest().body(new ErrorResponse(message, List.of(message)));
            }

            if (cause instanceof MismatchedInputException mismatchedInputException) {
                String fieldPath = buildFieldPath(mismatchedInputException.getPath().stream()
                        .map(reference -> reference.getFieldName())
                        .toList());
                String message = fieldPath.isBlank()
                        ? "El cuerpo de la solicitud tiene un formato invalido"
                        : "El campo " + fieldPath + " tiene un formato invalido";
                return ResponseEntity.badRequest().body(new ErrorResponse(message, List.of(message)));
            }
        } catch (Exception ignored) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "El cuerpo de la solicitud no es valido",
                    List.of("Revisa la estructura y los tipos de datos enviados")
            ));
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(
                "El cuerpo de la solicitud no es valido",
                List.of("Revisa la estructura y los tipos de datos enviados")
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String message = resolveIllegalArgumentMessage(ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(message, List.of(message)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(
            "Ocurrio un error inesperado en el servidor",
            List.of("Intenta nuevamente o revisa los datos enviados")
        ));
    }

    private boolean isNumericTarget(Class<?> targetType) {
        return targetType != null
                && (Number.class.isAssignableFrom(targetType)
                || targetType == int.class
                || targetType == long.class
                || targetType == double.class
                || targetType == float.class
                || targetType == short.class);
    }

    private String buildFieldPath(List<String> pathParts) {
        return pathParts.stream()
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining("."));
    }

    private String resolveInvalidFormatMessage(String fieldPath, Class<?> targetType) {
        if (fieldPath == null || fieldPath.isBlank()) {
            return "El cuerpo de la solicitud tiene un formato invalido";
        }

        if (isNumericTarget(targetType)) {
            return "El campo " + fieldPath + " debe ser numerico";
        }

        if (fieldPath.endsWith("priority")) {
            return "El campo " + fieldPath + " debe ser COST o TIME";
        }

        if (fieldPath.endsWith("weightUnit")) {
            return "El campo " + fieldPath + " debe ser GRAMS, KILOGRAMS o POUNDS";
        }

        if (fieldPath.endsWith("currency")) {
            return "El campo " + fieldPath + " debe ser COP";
        }

        return "El campo " + fieldPath + " tiene un formato invalido";
    }

    private String resolveIllegalArgumentMessage(IllegalArgumentException ex) {
        String originalMessage = ex.getMessage();

        if (originalMessage == null || originalMessage.isBlank()) {
            return "La solicitud contiene un valor invalido";
        }

        if (originalMessage.contains("Prioridad")) {
            return "La prioridad debe ser COST o TIME";
        }

        if (originalMessage.contains("UnidadPeso")) {
            return "La unidad de peso debe ser GRAMS, KILOGRAMS o POUNDS";
        }

        return originalMessage;
    }

    public record ErrorResponse(String message, List<String> errors) {}
}
