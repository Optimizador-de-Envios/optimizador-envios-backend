package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfirmacionPedidoServiceTest {

    private ConfirmacionPedidoService confirmacionPedidoService;

    @BeforeEach
    void setUp() {
        confirmacionPedidoService = new ConfirmacionPedidoService();
    }

    @Test
    void dadoProveedorNoSeleccionado_cuandoSeConfirmaPedido_entoncesDebeLanzarExcepcion() {
        Pedido pedido = buildPedido();

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> confirmacionPedidoService.confirmar(pedido, null, cotizacionesDisponibles(), 148.3)
        );

        assertEquals("Se debe seleccionar un proveedor para continuar", exception.getMessage());
    }

    @Test
    void dadoProveedorSeleccionadoQueNoCoincideConCotizaciones_cuandoSeConfirmaPedido_entoncesDebeLanzarExcepcion() {
        Pedido pedido = buildPedido();
        Cotizacion seleccionInvalida = new Cotizacion("Local", 30387.59, "COP", 1);

        PedidoInvalidoException exception = assertThrows(
                PedidoInvalidoException.class,
                () -> confirmacionPedidoService.confirmar(pedido, seleccionInvalida, cotizacionesDisponibles(), 148.3)
        );

        assertEquals("La opcion seleccionada no coincide con las cotizaciones disponibles", exception.getMessage());
    }

    @Test
    void dadoProveedorSeleccionadoValido_cuandoSeConfirmaPedido_entoncesDebeConstruirConfirmacionPendienteDePersistencia() {
        Pedido pedido = buildPedido();
        List<Cotizacion> cotizacionesDisponibles = cotizacionesDisponibles();
        Cotizacion seleccionValida = new Cotizacion("Local", 30386.59, "COP", 1);

        ConfirmacionPedido confirmacion = confirmacionPedidoService.confirmar(
                pedido,
                seleccionValida,
            cotizacionesDisponibles,
                148.3
        );

        assertNull(confirmacion.id());
        assertSame(pedido, confirmacion.pedido());
        assertEquals(148.3, confirmacion.distanciaKm());
        assertSame(cotizacionesDisponibles.get(2), confirmacion.opcionSeleccionada());
        assertEquals("Local", confirmacion.opcionSeleccionada().nombreProveedor());
        assertEquals(30386.59, confirmacion.opcionSeleccionada().costo());
        assertEquals("COP", confirmacion.opcionSeleccionada().moneda());
        assertEquals(1, confirmacion.opcionSeleccionada().diasEntrega());
    }

    private Pedido buildPedido() {
        return new Pedido(
                new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                new Ubicacion("Bogota, DC, Colombia", 4.635456, -74.08768),
                10.0,
                UnidadPeso.KILOGRAMS,
                Prioridad.COST
        );
    }

    private List<Cotizacion> cotizacionesDisponibles() {
        return List.of(
                new Cotizacion("FedEx", 42100.0, "COP", 1),
                new Cotizacion("DHL", 35500.0, "COP", 1),
                new Cotizacion("Local", 30386.59, "COP", 1)
        );
    }
}