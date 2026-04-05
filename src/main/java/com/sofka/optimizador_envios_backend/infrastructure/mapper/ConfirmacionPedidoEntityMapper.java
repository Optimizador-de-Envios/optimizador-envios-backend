package com.sofka.optimizador_envios_backend.infrastructure.mapper;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacionPedidoEntityMapper {

    public ConfirmacionPedidoEntity toEntity(ConfirmacionPedido confirmacion) {
    return ConfirmacionPedidoEntity.of(
        confirmacion.id(),
        confirmacion.userId(),
        confirmacion.confirmationToken().value(),
        confirmacion.pedido().origen().nombre(),
        confirmacion.pedido().origen().lat(),
        confirmacion.pedido().origen().lng(),
        confirmacion.pedido().destino().nombre(),
        confirmacion.pedido().destino().lat(),
        confirmacion.pedido().destino().lng(),
        confirmacion.pedido().peso(),
        confirmacion.pedido().unidadPeso().name(),
        confirmacion.pedido().prioridad().name(),
        confirmacion.distanciaKm(),
        confirmacion.opcionSeleccionada().nombreProveedor(),
        confirmacion.opcionSeleccionada().costo(),
        confirmacion.opcionSeleccionada().moneda(),
        confirmacion.opcionSeleccionada().diasEntrega(),
        confirmacion.createdAt()
    );
    }

    public ConfirmacionPedido toDomain(ConfirmacionPedidoEntity entity) {
        Pedido pedido = new Pedido(
                new Ubicacion(entity.getOriginName(), entity.getOriginLat(), entity.getOriginLng()),
                new Ubicacion(entity.getDestinationName(), entity.getDestinationLat(), entity.getDestinationLng()),
                entity.getWeight(),
                UnidadPeso.valueOf(entity.getWeightUnit()),
                Prioridad.valueOf(entity.getPriority())
        );

        Cotizacion cotizacion = new Cotizacion(
                entity.getSelectedProviderName(),
                entity.getSelectedCost(),
                entity.getSelectedCurrency(),
                entity.getSelectedEstimatedDays()
        );

        return new ConfirmacionPedido(
            entity.getId(),
            entity.getUserId(),
            ConfirmationToken.of(entity.getConfirmationToken()),
            pedido,
            entity.getDistanceKm(),
            cotizacion,
            entity.getCreatedAt()
        );
    }
}