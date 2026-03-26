package com.sofka.optimizador_envios_backend.infrastructure.mapper;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.CotizacionDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.RecomendacionResponseDto;

import java.util.List;

public class PedidoMapper {

    private PedidoMapper() {}

    public static Pedido toDomain(PedidoRequestDto dto) {
        var order = dto.order();
        Ubicacion origen  = new Ubicacion(order.origin().name(),      order.origin().lat(),      order.origin().lng());
        Ubicacion destino = new Ubicacion(order.destination().name(), order.destination().lat(), order.destination().lng());
        return new Pedido(
                origen,
                destino,
                order.weight(),
                UnidadPeso.valueOf(order.weightUnit()),
                Prioridad.valueOf(order.priority())
        );
    }

    public static RecomendacionResponseDto toResponseDto(Recomendacion recomendacion) {
        CotizacionDto recomendada = toDto(recomendacion.recomendada());
        List<CotizacionDto> alternativas = recomendacion.alternativas().stream()
                .map(PedidoMapper::toDto)
                .toList();
        return new RecomendacionResponseDto(recomendada, alternativas);
    }

    private static CotizacionDto toDto(Cotizacion cotizacion) {
        return new CotizacionDto(
                cotizacion.nombreProveedor(),
                cotizacion.costo(),
                "COP",
                cotizacion.diasEntrega()
        );
    }
}
