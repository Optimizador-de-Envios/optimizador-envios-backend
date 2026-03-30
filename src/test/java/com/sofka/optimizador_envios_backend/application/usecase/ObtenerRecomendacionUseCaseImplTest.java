package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.domain.model.*;
import com.sofka.optimizador_envios_backend.domain.service.EstrategiaFactory;
import com.sofka.optimizador_envios_backend.domain.service.MotorRecomendacionService;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerRecomendacionUseCaseImplTest {

    @Mock
    private DistanciaClient distanciaClient;

    @Mock
    private ProveedorClient fedexClient;

    @Mock
    private ProveedorClient dhlClient;

    @Mock
    private ProveedorClient localClient;

    private ObtenerRecomendacionUseCaseImpl useCase;

    private final Pedido pedidoCosto = new Pedido(
            new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
            new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
            5.0,
            UnidadPeso.KILOGRAMS,
            Prioridad.COST
    );

    @BeforeEach
    void setUp() {
        useCase = new ObtenerRecomendacionUseCaseImpl(
                distanciaClient,
                List.of(fedexClient, dhlClient, localClient),
                new MotorRecomendacionService(new EstrategiaFactory())
        );
    }

    @Test
    void dadoUnPedidoValido_cuandoSeObtieneLaRecomendacion_entoncesDebeConsultarLaDistancia() {
        when(distanciaClient.obtenerDistanciaKm(pedidoCosto.origen(), pedidoCosto.destino())).thenReturn(150.0);
        when(fedexClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("FedEx", 28000.0, "COP", 1));
        when(dhlClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("DHL", 22500.0, "COP", 1));
        when(localClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("Local", 18000.0, "COP", 2));

        useCase.obtenerRecomendacion(pedidoCosto);

        verify(distanciaClient).obtenerDistanciaKm(pedidoCosto.origen(), pedidoCosto.destino());
    }

    @Test
    void dadoUnPedidoValido_cuandoSeObtieneLaRecomendacion_entoncesDebeConsultarTodosLosProveedores() {
        when(distanciaClient.obtenerDistanciaKm(pedidoCosto.origen(), pedidoCosto.destino())).thenReturn(150.0);
        when(fedexClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("FedEx", 28000.0, "COP", 1));
        when(dhlClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("DHL", 22500.0, "COP", 1));
        when(localClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("Local", 18000.0, "COP", 2));

        useCase.obtenerRecomendacion(pedidoCosto);

        verify(fedexClient).cotizar(pedidoCosto, 150.0);
        verify(dhlClient).cotizar(pedidoCosto, 150.0);
        verify(localClient).cotizar(pedidoCosto, 150.0);
    }

    @Test
    void dadoPrioridadCosto_cuandoSeObtieneLaRecomendacion_entoncesDebeRetornarMenorCosto() {
        when(distanciaClient.obtenerDistanciaKm(pedidoCosto.origen(), pedidoCosto.destino())).thenReturn(150.0);
        when(fedexClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("FedEx", 28000.0, "COP", 1));
        when(dhlClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("DHL", 22500.0, "COP", 1));
        when(localClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("Local", 18000.0, "COP", 2));

        Recomendacion resultado = useCase.obtenerRecomendacion(pedidoCosto);

        assertEquals("Local", resultado.recomendada().nombreProveedor());
    }

    @Test
    void dadoPrioridadTiempo_cuandoSeObtieneLaRecomendacion_entoncesDebeRetornarMenorTiempo() {
        Pedido pedidoTiempo = new Pedido(
                pedidoCosto.origen(), pedidoCosto.destino(),
                pedidoCosto.peso(), pedidoCosto.unidadPeso(), Prioridad.TIME
        );
        when(distanciaClient.obtenerDistanciaKm(pedidoTiempo.origen(), pedidoTiempo.destino())).thenReturn(150.0);
        when(fedexClient.cotizar(pedidoTiempo, 150.0)).thenReturn(new Cotizacion("FedEx", 28000.0, "COP", 1));
        when(dhlClient.cotizar(pedidoTiempo, 150.0)).thenReturn(new Cotizacion("DHL", 22500.0, "COP", 2));
        when(localClient.cotizar(pedidoTiempo, 150.0)).thenReturn(new Cotizacion("Local", 18000.0, "COP", 3));

        Recomendacion resultado = useCase.obtenerRecomendacion(pedidoTiempo);

        assertEquals("FedEx", resultado.recomendada().nombreProveedor());
    }

    @Test
    void dadoUnaRecomendacion_entoncesLasAlternativasNoDebenContenerLaRecomendada() {
        when(distanciaClient.obtenerDistanciaKm(pedidoCosto.origen(), pedidoCosto.destino())).thenReturn(150.0);
        when(fedexClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("FedEx", 28000.0, "COP", 1));
        when(dhlClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("DHL", 22500.0, "COP", 1));
        when(localClient.cotizar(pedidoCosto, 150.0)).thenReturn(new Cotizacion("Local", 18000.0, "COP", 2));

        Recomendacion resultado = useCase.obtenerRecomendacion(pedidoCosto);

        assertEquals(2, resultado.alternativas().size());
        assertFalse(resultado.alternativas().stream()
                .anyMatch(c -> c.nombreProveedor().equals(resultado.recomendada().nombreProveedor())));
    }
}
