package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.service.ConfirmacionPedidoService;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmarPedidoUseCaseImplTest {

    @Mock
    private DistanciaClient distanciaClient;

    @Mock
    private ProveedorClient fedexClient;

    @Mock
    private ProveedorClient dhlClient;

    @Mock
    private ProveedorClient localClient;

    @Mock
    private ConfirmacionPedidoService confirmacionPedidoService;

    @Mock
    private ConfirmacionPedidoRepository confirmacionPedidoRepository;

    private ConfirmarPedidoUseCaseImpl useCase;

    private final Pedido pedido = new Pedido(
            new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
            new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
            10.0,
            UnidadPeso.KILOGRAMS,
            Prioridad.COST
    );

    private final Cotizacion opcionSeleccionada = new Cotizacion("Local", 30386.59, "COP", 1);

    @BeforeEach
    void setUp() {
        CotizarPedidoService cotizarPedidoService = new CotizarPedidoService(
                distanciaClient,
                List.of(fedexClient, dhlClient, localClient)
        );

        useCase = new ConfirmarPedidoUseCaseImpl(
                cotizarPedidoService,
                confirmacionPedidoService,
                confirmacionPedidoRepository
        );
    }

    @Test
    void dadoPedidoValidoYOpcionSeleccionada_cuandoSeConfirma_entoncesDebeConsultarDistanciaYCotizarTodosLosProveedores() {
        Cotizacion fedex = new Cotizacion("FedEx", 42100.0, "COP", 1);
        Cotizacion dhl = new Cotizacion("DHL", 35500.0, "COP", 1);
        Cotizacion local = new Cotizacion("Local", 30386.59, "COP", 1);
        ConfirmacionPedido confirmacionPendiente = new ConfirmacionPedido(null, pedido, 148.3, local);
        ConfirmacionPedido confirmacionGuardada = new ConfirmacionPedido("abc-123", pedido, 148.3, local);

        when(distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino())).thenReturn(148.3);
        when(fedexClient.cotizar(pedido, 148.3)).thenReturn(fedex);
        when(dhlClient.cotizar(pedido, 148.3)).thenReturn(dhl);
        when(localClient.cotizar(pedido, 148.3)).thenReturn(local);
        when(confirmacionPedidoService.confirmar(pedido, opcionSeleccionada, List.of(fedex, dhl, local), 148.3))
                .thenReturn(confirmacionPendiente);
        when(confirmacionPedidoRepository.guardar(confirmacionPendiente)).thenReturn(confirmacionGuardada);

        ConfirmacionPedido resultado = useCase.confirmar(pedido, opcionSeleccionada);

        verify(distanciaClient).obtenerDistanciaKm(pedido.origen(), pedido.destino());
        verify(fedexClient).cotizar(pedido, 148.3);
        verify(dhlClient).cotizar(pedido, 148.3);
        verify(localClient).cotizar(pedido, 148.3);
        verify(confirmacionPedidoService).confirmar(pedido, opcionSeleccionada, List.of(fedex, dhl, local), 148.3);
        verify(confirmacionPedidoRepository).guardar(confirmacionPendiente);
        assertSame(confirmacionGuardada, resultado);
        assertEquals("abc-123", resultado.id());
    }

    @Test
    void dadoOpcionSeleccionadaInvalida_cuandoSeConfirma_entoncesNoDebePersistirYDebePropagarLaExcepcion() {
        Cotizacion fedex = new Cotizacion("FedEx", 42100.0, "COP", 1);
        Cotizacion dhl = new Cotizacion("DHL", 35500.0, "COP", 1);
        Cotizacion local = new Cotizacion("Local", 30386.59, "COP", 1);

        when(distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino())).thenReturn(148.3);
        when(fedexClient.cotizar(pedido, 148.3)).thenReturn(fedex);
        when(dhlClient.cotizar(pedido, 148.3)).thenReturn(dhl);
        when(localClient.cotizar(pedido, 148.3)).thenReturn(local);
        when(confirmacionPedidoService.confirmar(pedido, opcionSeleccionada, List.of(fedex, dhl, local), 148.3))
                .thenThrow(new PedidoInvalidoException("La opcion seleccionada no coincide con las cotizaciones disponibles"));

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> useCase.confirmar(pedido, opcionSeleccionada)
        );

        assertEquals("La opcion seleccionada no coincide con las cotizaciones disponibles", exception.getMessage());
        verify(confirmacionPedidoRepository, never()).guardar(org.mockito.ArgumentMatchers.any());
    }
}