package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;

import java.util.Comparator;
import java.util.List;

public class MotorRecomendacionService {

    public Recomendacion recomendar(List<Cotizacion> cotizaciones, Prioridad prioridad) {
        Cotizacion recomendada = seleccionar(cotizaciones, prioridad);

        List<Cotizacion> alternativas = cotizaciones.stream()
                .filter(c -> !c.nombreProveedor().equals(recomendada.nombreProveedor()))
                .toList();

        return new Recomendacion(recomendada, alternativas);
    }

    private Cotizacion seleccionar(List<Cotizacion> cotizaciones, Prioridad prioridad) {
        Comparator<Cotizacion> comparador = switch (prioridad) {
            case COST -> Comparator.comparingDouble(Cotizacion::costo)
                    .thenComparingInt(Cotizacion::diasEntrega);
            case TIME -> Comparator.comparingInt(Cotizacion::diasEntrega)
                    .thenComparingDouble(Cotizacion::costo);
        };

        return cotizaciones.stream()
                .min(comparador)
                .orElseThrow();
    }
}
