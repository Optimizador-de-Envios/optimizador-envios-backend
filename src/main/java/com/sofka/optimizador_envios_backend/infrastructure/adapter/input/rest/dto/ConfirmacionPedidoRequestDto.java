package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ConfirmacionPedidoRequestDto(
        @NotNull @Valid OrderDto order,
        @NotNull @Valid SelectedOptionDto selectedOption
) {}