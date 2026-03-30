package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OrderDto(
        @NotNull(message = "El origen es obligatorio") @Valid UbicacionDto origin,
        @NotNull(message = "El destino es obligatorio") @Valid UbicacionDto destination,
        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.001", message = "El peso debe ser mayor o igual a 0.001")
        @DecimalMax(value = "70.0", message = "El peso debe ser menor o igual a 70")
        Double weight,
        @NotBlank(message = "La unidad de peso es obligatoria")
        @Pattern(regexp = "GRAMS|KILOGRAMS|POUNDS",
                message = "La unidad de peso debe ser GRAMS, KILOGRAMS o POUNDS")
        String weightUnit,
        @NotBlank(message = "La prioridad es obligatoria")
        @Pattern(regexp = "COST|TIME", message = "La prioridad debe ser COST o TIME")
        String priority
) {}
