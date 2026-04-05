package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerMisPedidosUseCaseImplTest {

    @Mock
    private ConfirmacionPedidoRepository confirmacionPedidoRepository;

    private ObtenerMisPedidosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerMisPedidosUseCaseImpl(confirmacionPedidoRepository);
    }

    @Test
    void dadoUsuarioAutenticado_cuandoConsultaSuHistorial_entoncesDebeRetornarSoloSusPedidos() {
        ConfirmacionPedido primerPedido = new ConfirmacionPedido(
                "abc-123",
                "user-123",
                ConfirmationToken.of("token-123"),
                new Pedido(
                        new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                        new Ubicacion("Bogota, DC, Colombia", 4.635456, -74.08768),
                        10.0,
                        UnidadPeso.KILOGRAMS,
                        Prioridad.COST
                ),
                148.3,
                new Cotizacion("Local", 30386.59, "COP", 1)
        );
        ConfirmacionPedido segundoPedido = new ConfirmacionPedido(
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
                new Cotizacion("DHL", 35500.0, "COP", 1)
        );

        when(confirmacionPedidoRepository.buscarPorUserId("user-123")).thenReturn(List.of(primerPedido, segundoPedido));

        List<ConfirmacionPedido> resultado = useCase.obtenerMisPedidos("user-123");

        verify(confirmacionPedidoRepository).buscarPorUserId("user-123");
        assertEquals(2, resultado.size());
        assertSame(primerPedido, resultado.get(0));
        assertSame(segundoPedido, resultado.get(1));
        assertEquals("user-123", resultado.get(0).userId());
        assertEquals("user-123", resultado.get(1).userId());
    }
}
