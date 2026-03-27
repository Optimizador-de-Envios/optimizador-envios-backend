package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;

import java.util.Comparator;
import java.util.List;

public class EstrategiaTiempo implements EstrategiaRecomendacion {

    @Override
    public Cotizacion seleccionar(List<Cotizacion> cotizaciones) {
        return cotizaciones.stream()
                .min(Comparator.comparingInt(Cotizacion::diasEntrega)
                        .thenComparingDouble(Cotizacion::costo))
                .orElseThrow();
    }
}
