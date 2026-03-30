package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SelectedOptionDto(
        @NotNull String providerName,
        @NotNull Double cost,
        @NotNull String currency,
        @NotNull Integer estimatedDays
) {}