package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PedidoRequestDto(@NotNull(message = "La orden es obligatoria") @Valid OrderDto order) {}
