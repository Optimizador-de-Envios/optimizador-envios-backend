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
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmarPedidoUseCaseImplTest {

        private static final String CONFIRMATION_TOKEN = "token-123";

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
        ConfirmationToken token = ConfirmationToken.of(CONFIRMATION_TOKEN);
        ConfirmacionPedido confirmacionPendiente = new ConfirmacionPedido(null, token, pedido, 148.3, local);
        ConfirmacionPedido confirmacionGuardada = new ConfirmacionPedido("abc-123", token, pedido, 148.3, local);

        when(confirmacionPedidoRepository.buscarPorTokenConfirmacion(CONFIRMATION_TOKEN)).thenReturn(Optional.empty());
        when(distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino())).thenReturn(148.3);
        when(fedexClient.cotizar(pedido, 148.3)).thenReturn(fedex);
        when(dhlClient.cotizar(pedido, 148.3)).thenReturn(dhl);
        when(localClient.cotizar(pedido, 148.3)).thenReturn(local);
        when(confirmacionPedidoService.confirmar(eq(token), eq(pedido), eq(opcionSeleccionada), eq(List.of(fedex, dhl, local)), eq(148.3)))
                .thenReturn(confirmacionPendiente);
        when(confirmacionPedidoRepository.guardar(confirmacionPendiente)).thenReturn(confirmacionGuardada);

        ConfirmacionPedido resultado = useCase.confirmar(CONFIRMATION_TOKEN, pedido, opcionSeleccionada);

        verify(confirmacionPedidoService).validarTokenConfirmacion(CONFIRMATION_TOKEN);
        verify(confirmacionPedidoRepository).buscarPorTokenConfirmacion(CONFIRMATION_TOKEN);
        verify(distanciaClient).obtenerDistanciaKm(pedido.origen(), pedido.destino());
        verify(fedexClient).cotizar(pedido, 148.3);
        verify(dhlClient).cotizar(pedido, 148.3);
        verify(localClient).cotizar(pedido, 148.3);
                verify(confirmacionPedidoService).confirmar(token, pedido, opcionSeleccionada, List.of(fedex, dhl, local), 148.3);
        verify(confirmacionPedidoRepository).guardar(confirmacionPendiente);
        assertSame(confirmacionGuardada, resultado);
        assertEquals("abc-123", resultado.id());
                assertEquals(CONFIRMATION_TOKEN, resultado.confirmationToken().value());
    }

    @Test
    void dadoOpcionSeleccionadaInvalida_cuandoSeConfirma_entoncesNoDebePersistirYDebePropagarLaExcepcion() {
        Cotizacion fedex = new Cotizacion("FedEx", 42100.0, "COP", 1);
        Cotizacion dhl = new Cotizacion("DHL", 35500.0, "COP", 1);
        Cotizacion local = new Cotizacion("Local", 30386.59, "COP", 1);
        ConfirmationToken token = ConfirmationToken.of(CONFIRMATION_TOKEN);

        when(confirmacionPedidoRepository.buscarPorTokenConfirmacion(CONFIRMATION_TOKEN)).thenReturn(Optional.empty());
        when(distanciaClient.obtenerDistanciaKm(pedido.origen(), pedido.destino())).thenReturn(148.3);
        when(fedexClient.cotizar(pedido, 148.3)).thenReturn(fedex);
        when(dhlClient.cotizar(pedido, 148.3)).thenReturn(dhl);
        when(localClient.cotizar(pedido, 148.3)).thenReturn(local);
        when(confirmacionPedidoService.confirmar(eq(token), eq(pedido), eq(opcionSeleccionada), eq(List.of(fedex, dhl, local)), eq(148.3)))
                .thenThrow(new PedidoInvalidoException("La opcion seleccionada no coincide con las cotizaciones disponibles"));

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> useCase.confirmar(CONFIRMATION_TOKEN, pedido, opcionSeleccionada)
        );

        assertEquals("La opcion seleccionada no coincide con las cotizaciones disponibles", exception.getMessage());
        verify(confirmacionPedidoRepository).buscarPorTokenConfirmacion(CONFIRMATION_TOKEN);
        verify(confirmacionPedidoRepository, never()).guardar(any());
    }

    @Test
    void dadoConfirmationTokenYaPersistido_cuandoSeConfirmaMismoIntento_entoncesDebeRetornarLaMismaConfirmacionSinRecotizarNiPersistir() {
        ConfirmationToken token = ConfirmationToken.of(CONFIRMATION_TOKEN);
        ConfirmacionPedido confirmacionExistente = new ConfirmacionPedido(
                "abc-123",
                token,
                pedido,
                148.3,
                opcionSeleccionada
        );

        when(confirmacionPedidoRepository.buscarPorTokenConfirmacion(CONFIRMATION_TOKEN))
                .thenReturn(Optional.of(confirmacionExistente));

        ConfirmacionPedido resultado = useCase.confirmar(CONFIRMATION_TOKEN, pedido, opcionSeleccionada);

        verify(confirmacionPedidoRepository).buscarPorTokenConfirmacion(CONFIRMATION_TOKEN);
        verify(confirmacionPedidoRepository, never()).guardar(any());
                verify(confirmacionPedidoService, never()).confirmar(any(), any(), any(), any(), any(Double.class));
        verifyNoInteractions(distanciaClient, fedexClient, dhlClient, localClient);
        assertSame(confirmacionExistente, resultado);
    }
}