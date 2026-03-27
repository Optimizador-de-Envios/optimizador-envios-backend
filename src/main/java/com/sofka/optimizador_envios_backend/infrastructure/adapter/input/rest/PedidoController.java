package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest;

import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.RecomendacionResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedido")
public class PedidoController {

    private final ObtenerRecomendacionUseCase obtenerRecomendacionUseCase;
    private final PedidoMapper pedidoMapper;

    public PedidoController(ObtenerRecomendacionUseCase obtenerRecomendacionUseCase,
                            PedidoMapper pedidoMapper) {
        this.obtenerRecomendacionUseCase = obtenerRecomendacionUseCase;
        this.pedidoMapper = pedidoMapper;
    }

    @PostMapping
    public ResponseEntity<RecomendacionResponseDto> obtenerRecomendacion(
            @Valid @RequestBody PedidoRequestDto request) {
        Recomendacion recomendacion = obtenerRecomendacionUseCase
                .obtenerRecomendacion(pedidoMapper.toDomain(request));
        return ResponseEntity.ok(pedidoMapper.toResponseDto(recomendacion));
    }
}
