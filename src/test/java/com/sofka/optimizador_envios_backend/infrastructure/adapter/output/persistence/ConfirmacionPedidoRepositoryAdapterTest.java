package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.ConfirmacionPedidoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmacionPedidoRepositoryAdapterTest {

    @Mock
    private ConfirmacionPedidoJpaRepository jpaRepository;

    @Mock
    private ConfirmacionPedidoEntityMapper mapper;

    private ConfirmacionPedidoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ConfirmacionPedidoRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void dadaConfirmacionPendiente_cuandoSeGuarda_entoncesDebeMapearPersistirYRetornarElDominioGuardado() {
        ConfirmationToken token = ConfirmationToken.of("token-123");
        ConfirmacionPedido confirmacionPendiente = new ConfirmacionPedido(
                null,
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

        ConfirmacionPedidoEntity entity = ConfirmacionPedidoEntity.of(
            null,
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
        ConfirmacionPedidoEntity savedEntity = ConfirmacionPedidoEntity.of(
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
        ConfirmacionPedido confirmacionGuardada = new ConfirmacionPedido(
                "abc-123",
            token,
                confirmacionPendiente.pedido(),
                148.3,
                confirmacionPendiente.opcionSeleccionada()
        );

        when(mapper.toEntity(confirmacionPendiente)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(confirmacionGuardada);

        ConfirmacionPedido resultado = adapter.guardar(confirmacionPendiente);

        verify(mapper).toEntity(confirmacionPendiente);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
        assertSame(confirmacionGuardada, resultado);
    }

    @Test
    void dadoTokenConfirmacionPersistido_cuandoSeBuscaPorToken_entoncesDebeRetornarLaConfirmacionEncontrada() {
        ConfirmationToken token = ConfirmationToken.of("token-123");
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

        when(jpaRepository.findByConfirmationToken("token-123")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(confirmacion);

        Optional<ConfirmacionPedido> resultado = adapter.buscarPorTokenConfirmacion("token-123");

        verify(jpaRepository).findByConfirmationToken("token-123");
        verify(mapper).toDomain(entity);
        assertTrue(resultado.isPresent());
        assertSame(confirmacion, resultado.get());
    }

        @Test
        void dadoUsuarioAutenticado_cuandoSeBuscaSuHistorial_entoncesDebeFiltrarPorUserId() {
        ConfirmacionPedidoEntity entityUno = ConfirmacionPedidoEntity.of(
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
        ConfirmacionPedidoEntity entityDos = ConfirmacionPedidoEntity.of(
            "def-456",
            "user-123",
            "token-456",
            "Medellin, ANT, Colombia",
            6.2442,
            -75.5812,
            "Cali, VAC, Colombia",
            3.4516,
            -76.5320,
            5.0,
            "KILOGRAMS",
            "TIME",
            298.1,
            "DHL",
            35500.0,
            "COP",
            1,
            Instant.parse("2026-04-03T19:00:00Z")
        );
        ConfirmacionPedido confirmacionUno = new ConfirmacionPedido(
            "abc-123",
            "user-123",
            ConfirmationToken.of("token-123"),
            new Pedido(
                new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
                10.0,
                UnidadPeso.KILOGRAMS,
                Prioridad.COST
            ),
            148.3,
            new Cotizacion("Local", 30386.59, "COP", 1),
            Instant.parse("2026-04-03T18:35:00Z")
        );
        ConfirmacionPedido confirmacionDos = new ConfirmacionPedido(
            "def-456",
            "user-123",
            ConfirmationToken.of("token-456"),
            new Pedido(
                new Ubicacion("Medellin, ANT, Colombia", 6.2442, -75.5812),
                new Ubicacion("Cali, VAC, Colombia", 3.4516, -76.5320),
                5.0,
                UnidadPeso.KILOGRAMS,
                Prioridad.TIME
            ),
            298.1,
            new Cotizacion("DHL", 35500.0, "COP", 1),
            Instant.parse("2026-04-03T19:00:00Z")
        );

        when(jpaRepository.findByUserIdOrderByCreatedAtDesc("user-123")).thenReturn(List.of(entityUno, entityDos));
        when(mapper.toDomain(entityUno)).thenReturn(confirmacionUno);
        when(mapper.toDomain(entityDos)).thenReturn(confirmacionDos);

        List<ConfirmacionPedido> resultado = adapter.buscarPorUserId("user-123");

        verify(jpaRepository).findByUserIdOrderByCreatedAtDesc("user-123");
        verify(mapper).toDomain(entityUno);
        verify(mapper).toDomain(entityDos);
        assertEquals(2, resultado.size());
        assertSame(confirmacionUno, resultado.get(0));
        assertSame(confirmacionDos, resultado.get(1));
        }
}