package com.sofka.optimizador_envios_backend.infrastructure.mapper;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.CotizacionDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.RecomendacionResponseDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.SelectedOptionDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.UbicacionDto;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public Pedido toDomain(PedidoRequestDto dto) {
        return toDomain(dto.order());
    }

    public Pedido toDomain(ConfirmacionPedidoRequestDto dto) {
        return toDomain(dto.order());
    }

    public Cotizacion toDomain(SelectedOptionDto dto) {
        return new Cotizacion(
                dto.providerName(),
                dto.cost(),
                dto.currency(),
                dto.estimatedDays()
        );
    }

    public RecomendacionResponseDto toResponseDto(Recomendacion recomendacion) {
        CotizacionDto recomendada = toDto(recomendacion.recomendada());
        List<CotizacionDto> alternativas = recomendacion.alternativas().stream()
                .map(this::toDto)
                .toList();
        return new RecomendacionResponseDto(recomendada, alternativas);
    }

    public ConfirmacionPedidoResponseDto toResponseDto(ConfirmacionPedido confirmacionPedido) {
        return new ConfirmacionPedidoResponseDto(
                confirmacionPedido.id(),
                toDto(confirmacionPedido.pedido().origen()),
                toDto(confirmacionPedido.pedido().destino()),
                confirmacionPedido.pedido().peso(),
                confirmacionPedido.pedido().unidadPeso().name(),
                confirmacionPedido.pedido().prioridad().name(),
                confirmacionPedido.distanciaKm(),
                toDto(confirmacionPedido.opcionSeleccionada())
        );
    }

    private Pedido toDomain(com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.OrderDto order) {
        Ubicacion origen = new Ubicacion(order.origin().name(), order.origin().lat(), order.origin().lng());
        Ubicacion destino = new Ubicacion(order.destination().name(), order.destination().lat(), order.destination().lng());
        return new Pedido(
                origen,
                destino,
                order.weight(),
                UnidadPeso.valueOf(order.weightUnit()),
                Prioridad.valueOf(order.priority())
        );
    }

    private CotizacionDto toDto(Cotizacion cotizacion) {
        return new CotizacionDto(
                cotizacion.nombreProveedor(),
                cotizacion.costo(),
                cotizacion.moneda(),
                cotizacion.diasEntrega()
        );
    }

    private UbicacionDto toDto(Ubicacion ubicacion) {
        return new UbicacionDto(ubicacion.nombre(), ubicacion.lat(), ubicacion.lng());
    }
}
