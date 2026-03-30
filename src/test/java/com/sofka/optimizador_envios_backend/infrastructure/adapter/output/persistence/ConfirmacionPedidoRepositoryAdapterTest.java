package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence;

import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.ConfirmacionPedidoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
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
        ConfirmacionPedido confirmacionPendiente = new ConfirmacionPedido(
                null,
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

        ConfirmacionPedidoEntity entity = new ConfirmacionPedidoEntity();
        ConfirmacionPedidoEntity savedEntity = new ConfirmacionPedidoEntity();
        ConfirmacionPedido confirmacionGuardada = new ConfirmacionPedido(
                "abc-123",
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
}