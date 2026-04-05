package com.sofka.optimizador_envios_backend.infrastructure.mapper;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfirmacionPedidoEntityMapperTest {

    private ConfirmacionPedidoEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConfirmacionPedidoEntityMapper();
    }

    @Test
    void dadaConfirmacionDeDominio_cuandoSeMapeaAEntidad_entoncesDebeCopiarTodosLosCampos() {
        ConfirmationToken token = ConfirmationToken.of("token-123");
        ConfirmacionPedido confirmacion = new ConfirmacionPedido(
                "abc-123",
            token,
                new Pedido(
                        new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                        new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
                        10.0,
                        UnidadPeso.KILOGRAMS,
                        Prioridad.COST
                ),
                148.3,
                new Cotizacion("Local", 30386.59, "COP", 1)
        );

        ConfirmacionPedidoEntity entity = mapper.toEntity(confirmacion);

        assertEquals("abc-123", entity.getId());
    assertEquals("token-123", entity.getConfirmationToken());
        assertEquals("Tunja, BY, Colombia", entity.getOriginName());
        assertEquals(5.53528, entity.getOriginLat());
        assertEquals(-73.36778, entity.getOriginLng());
        assertEquals("Bogotá, DC, Colombia", entity.getDestinationName());
        assertEquals(4.635456, entity.getDestinationLat());
        assertEquals(-74.08768, entity.getDestinationLng());
        assertEquals(10.0, entity.getWeight());
        assertEquals("KILOGRAMS", entity.getWeightUnit());
        assertEquals("COST", entity.getPriority());
        assertEquals(148.3, entity.getDistanceKm());
        assertEquals("Local", entity.getSelectedProviderName());
        assertEquals(30386.59, entity.getSelectedCost());
        assertEquals("COP", entity.getSelectedCurrency());
        assertEquals(1, entity.getSelectedEstimatedDays());
    }

    @Test
    void dadaConfirmacionDeDominioConOwnerYCreacion_cuandoSeMapeaAEntidad_entoncesDebeCopiarUserIdYCreatedAt() {
        ConfirmationToken token = ConfirmationToken.of("token-123");
        Instant createdAt = Instant.parse("2026-04-03T18:35:00Z");
        ConfirmacionPedido confirmacion = new ConfirmacionPedido(
                "abc-123",
                "user-123",
                token,
                new Pedido(
                        new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                        new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
                        10.0,
                        UnidadPeso.KILOGRAMS,
                        Prioridad.COST
                ),
                148.3,
                new Cotizacion("Local", 30386.59, "COP", 1),
                createdAt
        );

        ConfirmacionPedidoEntity entity = mapper.toEntity(confirmacion);

        assertEquals("user-123", entity.getUserId());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void dadaEntidadPersistida_cuandoSeMapeaADominio_entoncesDebeReconstruirLaConfirmacionCompleta() {
        ConfirmacionPedidoEntity entity = ConfirmacionPedidoEntity.of(
            "abc-123",
            "token-123",
            "Tunja, BY, Colombia",
            5.53528,
            -73.36778,
            "Bogotá, DC, Colombia",
            4.635456,
            -74.08768,
            10.0,
            "KILOGRAMS",
            "COST",
            148.3,
            "Local",
            30386.59,
            "COP",
            1
        );

        ConfirmacionPedido confirmacion = mapper.toDomain(entity);

        assertEquals("abc-123", confirmacion.id());
    assertEquals("token-123", confirmacion.confirmationToken().value());
        assertEquals("Tunja, BY, Colombia", confirmacion.pedido().origen().nombre());
        assertEquals(5.53528, confirmacion.pedido().origen().lat());
        assertEquals(-73.36778, confirmacion.pedido().origen().lng());
        assertEquals("Bogotá, DC, Colombia", confirmacion.pedido().destino().nombre());
        assertEquals(4.635456, confirmacion.pedido().destino().lat());
        assertEquals(-74.08768, confirmacion.pedido().destino().lng());
        assertEquals(10.0, confirmacion.pedido().peso());
        assertEquals(UnidadPeso.KILOGRAMS, confirmacion.pedido().unidadPeso());
        assertEquals(Prioridad.COST, confirmacion.pedido().prioridad());
        assertEquals(148.3, confirmacion.distanciaKm());
        assertEquals("Local", confirmacion.opcionSeleccionada().nombreProveedor());
        assertEquals(30386.59, confirmacion.opcionSeleccionada().costo());
        assertEquals("COP", confirmacion.opcionSeleccionada().moneda());
        assertEquals(1, confirmacion.opcionSeleccionada().diasEntrega());
    }

    @Test
    void dadaEntidadPersistidaConOwnerYCreacion_cuandoSeMapeaADominio_entoncesDebeReconstruirUserIdYCreatedAt() {
        ConfirmacionPedidoEntity entity = ConfirmacionPedidoEntity.of(
                "abc-123",
                "user-123",
                "token-123",
                "Tunja, BY, Colombia",
                5.53528,
                -73.36778,
                "Bogotá, DC, Colombia",
                4.635456,
                -74.08768,
                10.0,
                "KILOGRAMS",
                "COST",
                148.3,
                "Local",
                30386.59,
                "COP",
                1,
                Instant.parse("2026-04-03T18:35:00Z")
        );

        ConfirmacionPedido confirmacion = mapper.toDomain(entity);

        assertEquals("user-123", confirmacion.userId());
        assertEquals(Instant.parse("2026-04-03T18:35:00Z"), confirmacion.createdAt());
    }
}