package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.sofka.optimizador_envios_backend.config.ProveedorConfig;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorClientMockTest {

    // FedEx: costoBase=10000, precioPorKg=5000, precioPorKm=200, kmPorDia=800
    private ProveedorConfig fedexConfig;
    // DHL:   costoBase=12000, precioPorKg=4500, precioPorKm=250, kmPorDia=900
    private ProveedorConfig dhlConfig;
    // Local: costoBase=8000,  precioPorKg=4000, precioPorKm=150, kmPorDia=600
    private ProveedorConfig localConfig;

    private final Ubicacion tunja  = new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778);
    private final Ubicacion bogota = new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768);

    @BeforeEach
    void setUp() {
        fedexConfig = new ProveedorConfig(10000, 5000, 200, 800);
        dhlConfig   = new ProveedorConfig(12000, 4500, 250, 900);
        localConfig = new ProveedorConfig(8000,  4000, 150, 600);
    }

    // ─────────────────────────────────────────────
    // FÓRMULAS CON VALORES REALES DEL YML
    // dist=150km, peso=5kg
    // FedEx: 10000 + (5×5000) + (150×200) = 65000
    // DHL:   12000 + (5×4500) + (150×250) = 72000
    // Local:  8000 + (5×4000) + (150×150) = 50500
    // ─────────────────────────────────────────────

    @Test
    void fedex_dadoPeso5KgYDistancia150Km_entoncesDebeAplicarFormulaDeCostoCorrectamente() {
        FedexClientMock fedex = new FedexClientMock(fedexConfig);
        Pedido pedido = new Pedido(tunja, bogota, 5.0, UnidadPeso.KILOGRAMS, Prioridad.COST);

        Cotizacion cotizacion = fedex.cotizar(pedido, 150.0);

        assertEquals(65000.0, cotizacion.costo());
        assertEquals(1, cotizacion.diasEntrega());
    }

    @Test
    void dhl_dadoPeso5KgYDistancia150Km_entoncesDebeAplicarFormulaDeCostoCorrectamente() {
        DhlClientMock dhl = new DhlClientMock(dhlConfig);
        Pedido pedido = new Pedido(tunja, bogota, 5.0, UnidadPeso.KILOGRAMS, Prioridad.COST);

        Cotizacion cotizacion = dhl.cotizar(pedido, 150.0);

        assertEquals(72000.0, cotizacion.costo());
        assertEquals(1, cotizacion.diasEntrega());
    }

    @Test
    void local_dadoPeso5KgYDistancia150Km_entoncesDebeAplicarFormulaDeCostoCorrectamente() {
        LocalClientMock local = new LocalClientMock(localConfig);
        Pedido pedido = new Pedido(tunja, bogota, 5.0, UnidadPeso.KILOGRAMS, Prioridad.COST);

        Cotizacion cotizacion = local.cotizar(pedido, 150.0);

        assertEquals(50500.0, cotizacion.costo());
        assertEquals(1, cotizacion.diasEntrega());
    }

    // ─────────────────────────────────────────────
    // CEIL — behavior no trivial
    // FedEx:  ceil(1000/800) = 2
    // Local:  ceil(700/600)  = 2
    // ─────────────────────────────────────────────

    @Test
    void fedex_dadoDistanciaMayorAKmPorDia_entoncesDebeRedondearDiasHaciaArriba() {
        FedexClientMock fedex = new FedexClientMock(fedexConfig);
        Pedido pedido = new Pedido(tunja, bogota, 5.0, UnidadPeso.KILOGRAMS, Prioridad.COST);

        Cotizacion cotizacion = fedex.cotizar(pedido, 1000.0);

        assertEquals(2, cotizacion.diasEntrega());
    }

    @Test
    void local_dadoDistanciaMayorAKmPorDia_entoncesDebeRedondearDiasHaciaArriba() {
        LocalClientMock local = new LocalClientMock(localConfig);
        Pedido pedido = new Pedido(tunja, bogota, 5.0, UnidadPeso.KILOGRAMS, Prioridad.COST);

        Cotizacion cotizacion = local.cotizar(pedido, 700.0);

        assertEquals(2, cotizacion.diasEntrega());
    }

    // ─────────────────────────────────────────────
    // CONVERSIÓN DE UNIDADES
    // Solo en FedEx — la lógica de conversión es compartida por todos
    // ─────────────────────────────────────────────

    @Test
    void fedex_dadoPesoEnGramos_entoncesDebeConvertirAKgParaElCalculo() {
        FedexClientMock fedex = new FedexClientMock(fedexConfig);
        // 5000g = 5kg → costo = 65000
        Pedido pedido = new Pedido(tunja, bogota, 5000.0, UnidadPeso.GRAMS, Prioridad.COST);

        Cotizacion cotizacion = fedex.cotizar(pedido, 150.0);

        assertEquals(65000.0, cotizacion.costo());
    }

    @Test
    void fedex_dadoPesoEnLibras_entoncesDebeConvertirAKgParaElCalculo() {
        FedexClientMock fedex = new FedexClientMock(fedexConfig);
        // 11.02311 lbs ≈ 5kg → costo ≈ 65000
        Pedido pedido = new Pedido(tunja, bogota, 11.02311, UnidadPeso.POUNDS, Prioridad.COST);

        Cotizacion cotizacion = fedex.cotizar(pedido, 150.0);

        assertEquals(65000.0, cotizacion.costo(), 1.0);
    }
}
