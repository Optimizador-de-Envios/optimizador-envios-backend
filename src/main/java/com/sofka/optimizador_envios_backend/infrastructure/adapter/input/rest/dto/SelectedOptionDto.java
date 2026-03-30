package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SelectedOptionDto(
        @NotBlank(message = "El nombre del proveedor es obligatorio") String providerName,
        @NotNull(message = "El costo es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0")
        Double cost,
        @NotBlank(message = "La moneda es obligatoria")
        @Pattern(regexp = "COP", message = "La moneda debe ser COP")
        String currency,
        @NotNull(message = "Los dias estimados son obligatorios")
        @Min(value = 1, message = "Los dias estimados deben ser mayores o iguales a 1")
        Integer estimatedDays
) {}