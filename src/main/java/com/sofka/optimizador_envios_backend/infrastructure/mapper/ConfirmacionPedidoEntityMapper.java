package com.sofka.optimizador_envios_backend.infrastructure.mapper;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacionPedidoEntityMapper {

    public ConfirmacionPedidoEntity toEntity(ConfirmacionPedido confirmacion) {
        ConfirmacionPedidoEntity entity = new ConfirmacionPedidoEntity();
        entity.setId(confirmacion.id());
        entity.setOriginName(confirmacion.pedido().origen().nombre());
        entity.setOriginLat(confirmacion.pedido().origen().lat());
        entity.setOriginLng(confirmacion.pedido().origen().lng());
        entity.setDestinationName(confirmacion.pedido().destino().nombre());
        entity.setDestinationLat(confirmacion.pedido().destino().lat());
        entity.setDestinationLng(confirmacion.pedido().destino().lng());
        entity.setWeight(confirmacion.pedido().peso());
        entity.setWeightUnit(confirmacion.pedido().unidadPeso().name());
        entity.setPriority(confirmacion.pedido().prioridad().name());
        entity.setDistanceKm(confirmacion.distanciaKm());
        entity.setSelectedProviderName(confirmacion.opcionSeleccionada().nombreProveedor());
        entity.setSelectedCost(confirmacion.opcionSeleccionada().costo());
        entity.setSelectedCurrency(confirmacion.opcionSeleccionada().moneda());
        entity.setSelectedEstimatedDays(confirmacion.opcionSeleccionada().diasEntrega());
        return entity;
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

        return new ConfirmacionPedido(entity.getId(), pedido, entity.getDistanceKm(), cotizacion);
    }
}