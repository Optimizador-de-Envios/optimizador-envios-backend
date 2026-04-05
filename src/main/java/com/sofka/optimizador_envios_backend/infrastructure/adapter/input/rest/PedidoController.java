package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest;

import com.sofka.optimizador_envios_backend.application.port.input.ConfirmarPedidoUseCase;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerMisPedidosUseCase;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoHistorialResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.RecomendacionResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedido")
public class PedidoController {

    private final ObtenerRecomendacionUseCase obtenerRecomendacionUseCase;
    private final ConfirmarPedidoUseCase confirmarPedidoUseCase;
        private final ObtenerMisPedidosUseCase obtenerMisPedidosUseCase;
    private final PedidoMapper pedidoMapper;

    public PedidoController(ObtenerRecomendacionUseCase obtenerRecomendacionUseCase,
                            ConfirmarPedidoUseCase confirmarPedidoUseCase,
                                                        ObtenerMisPedidosUseCase obtenerMisPedidosUseCase,
                            PedidoMapper pedidoMapper) {
        this.obtenerRecomendacionUseCase = obtenerRecomendacionUseCase;
        this.confirmarPedidoUseCase = confirmarPedidoUseCase;
                this.obtenerMisPedidosUseCase = obtenerMisPedidosUseCase;
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
                        @RequestAttribute("authenticatedUserId") String userId,
            @Valid @RequestBody ConfirmacionPedidoRequestDto request) {
        ConfirmacionPedido confirmacionPedido = confirmarPedidoUseCase.confirmar(
                                userId,
                request.confirmationToken(),
                pedidoMapper.toDomain(request),
                pedidoMapper.toDomain(request.selectedOption())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toResponseDto(confirmacionPedido));
    }

        @GetMapping("/mis-pedidos")
        public ResponseEntity<List<PedidoHistorialResponseDto>> obtenerMisPedidos(
                        @RequestAttribute("authenticatedUserId") String userId) {
                List<PedidoHistorialResponseDto> historial = obtenerMisPedidosUseCase.obtenerMisPedidos(userId)
                                .stream()
                                .map(pedidoMapper::toHistoryResponseDto)
                                .toList();

                return ResponseEntity.ok(historial);
        }
}
