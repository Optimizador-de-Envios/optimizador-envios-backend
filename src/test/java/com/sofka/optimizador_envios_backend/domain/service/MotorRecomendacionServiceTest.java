package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MotorRecomendacionServiceTest {

    private MotorRecomendacionService motorRecomendacionService;

    @BeforeEach
    void setUp() {
        motorRecomendacionService = new MotorRecomendacionService(new EstrategiaFactory());
    }

    // R5: Prioridad COST → recomendar menor costo
    @Test
    void dadoPrioridadCosto_cuandoHayOpcionesDisponibles_entoncesDebeRecomendarMenorCosto() {
        Cotizacion fedex = new Cotizacion("FedEx", 28000.0, 1);
        Cotizacion dhl   = new Cotizacion("DHL",   22500.0, 2);
        Cotizacion local = new Cotizacion("Local", 18000.0, 3);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex, dhl, local), Prioridad.COST
        );

        assertEquals("Local", resultado.recomendada().nombreProveedor());
    }

    // R6: Prioridad TIME → recomendar menor tiempo
    @Test
    void dadoPrioridadTiempo_cuandoHayOpcionesDisponibles_entoncesDebeRecomendarMenorTiempo() {
        Cotizacion fedex = new Cotizacion("FedEx", 28000.0, 1);
        Cotizacion dhl   = new Cotizacion("DHL",   22500.0, 2);
        Cotizacion local = new Cotizacion("Local", 18000.0, 3);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex, dhl, local), Prioridad.TIME
        );

        assertEquals("FedEx", resultado.recomendada().nombreProveedor());
    }

    // R7: Empate en costo → recomendar menor tiempo entre empatados
    @Test
    void dadoPrioridadCosto_cuandoHayEmpateDeCosto_entoncesDebeRecomendarMenorTiempoEntreEmpatados() {
        Cotizacion fedex = new Cotizacion("FedEx", 18000.0, 3);
        Cotizacion local = new Cotizacion("Local", 18000.0, 1);
        Cotizacion dhl   = new Cotizacion("DHL",   22500.0, 2);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex, local, dhl), Prioridad.COST
        );

        assertEquals("Local", resultado.recomendada().nombreProveedor());
    }

    // R8: Empate en tiempo → recomendar menor costo entre empatados
    @Test
    void dadoPrioridadTiempo_cuandoHayEmpateDeTiempo_entoncesDebeRecomendarMenorCostoEntreEmpatados() {
        Cotizacion fedex = new Cotizacion("FedEx", 28000.0, 1);
        Cotizacion local = new Cotizacion("Local", 18000.0, 1);
        Cotizacion dhl   = new Cotizacion("DHL",   22500.0, 2);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex, local, dhl), Prioridad.TIME
        );

        assertEquals("Local", resultado.recomendada().nombreProveedor());
    }

    // Las alternativas NO contienen la opción recomendada
    @Test
    void dadoRecomendacion_entoncesAlternativasNoDebenContenerLaRecomendada() {
        Cotizacion fedex = new Cotizacion("FedEx", 28000.0, 1);
        Cotizacion dhl   = new Cotizacion("DHL",   22500.0, 2);
        Cotizacion local = new Cotizacion("Local", 18000.0, 3);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex, dhl, local), Prioridad.COST
        );

        assertEquals(2, resultado.alternativas().size());
        assertFalse(
                resultado.alternativas().stream()
                        .anyMatch(c -> c.nombreProveedor().equals(resultado.recomendada().nombreProveedor()))
        );
    }

    // Con una sola opción, no hay alternativas
    @Test
    void dadoSoloUnaCotizacion_entoncesAlternativasDebenEstarVacias() {
        Cotizacion fedex = new Cotizacion("FedEx", 28000.0, 1);

        Recomendacion resultado = motorRecomendacionService.recomendar(
                List.of(fedex), Prioridad.COST
        );

        assertEquals("FedEx", resultado.recomendada().nombreProveedor());
        assertTrue(resultado.alternativas().isEmpty());
    }
}
