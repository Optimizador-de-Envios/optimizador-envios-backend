package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OrderDto(
        @NotNull @Valid UbicacionDto origin,
        @NotNull @Valid UbicacionDto destination,
        @NotNull @DecimalMin("0.001") @DecimalMax("70.0") Double weight,
        @NotNull String weightUnit,
        @NotNull String priority
) {}
