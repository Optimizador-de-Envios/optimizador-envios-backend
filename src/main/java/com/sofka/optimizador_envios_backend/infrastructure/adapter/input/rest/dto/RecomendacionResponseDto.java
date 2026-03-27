package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto;

import java.util.List;

public record RecomendacionResponseDto(CotizacionDto recommendation, List<CotizacionDto> alternatives) {
}
