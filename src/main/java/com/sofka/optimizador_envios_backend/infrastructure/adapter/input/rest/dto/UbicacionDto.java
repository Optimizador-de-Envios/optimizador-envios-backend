package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UbicacionDto(
	@NotBlank(message = "El nombre de la ubicacion es obligatorio") String name,
	@NotNull(message = "La latitud es obligatoria") Double lat,
	@NotNull(message = "La longitud es obligatoria") Double lng
) {}
