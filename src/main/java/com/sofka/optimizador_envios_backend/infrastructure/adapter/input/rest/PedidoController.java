package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest;

import com.sofka.optimizador_envios_backend.application.port.input.ConfirmarPedidoUseCase;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.RecomendacionResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedido")
public class PedidoController {

    private final ObtenerRecomendacionUseCase obtenerRecomendacionUseCase;
    private final ConfirmarPedidoUseCase confirmarPedidoUseCase;
    private final PedidoMapper pedidoMapper;

    public PedidoController(ObtenerRecomendacionUseCase obtenerRecomendacionUseCase,
                            ConfirmarPedidoUseCase confirmarPedidoUseCase,
                            PedidoMapper pedidoMapper) {
        this.obtenerRecomendacionUseCase = obtenerRecomendacionUseCase;
        this.confirmarPedidoUseCase = confirmarPedidoUseCase;
        this.pedidoMapper = pedidoMapper;
    }

    @PostMapping
    public ResponseEntity<RecomendacionResponseDto> obtenerRecomendacion(
            @Valid @RequestBody PedidoRequestDto request) {
        Recomendacion recomendacion = obtenerRecomendacionUseCase
                .obtenerRecomendacion(pedidoMapper.toDomain(request));
        return ResponseEntity.ok(pedidoMapper.toResponseDto(recomendacion));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<ConfirmacionPedidoResponseDto> confirmarPedido(
            @Valid @RequestBody ConfirmacionPedidoRequestDto request) {
        ConfirmacionPedido confirmacionPedido = confirmarPedidoUseCase.confirmar(
                request.confirmationToken(),
                pedidoMapper.toDomain(request),
                pedidoMapper.toDomain(request.selectedOption())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toResponseDto(confirmacionPedido));
    }
}
