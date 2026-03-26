package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;

import java.util.List;

public class MotorRecomendacionService {

    private final EstrategiaFactory estrategiaFactory;

    public MotorRecomendacionService() {
        this.estrategiaFactory = new EstrategiaFactory();
    }

    public Recomendacion recomendar(List<Cotizacion> cotizaciones, Prioridad prioridad) {
        EstrategiaRecomendacion estrategia = estrategiaFactory.obtener(prioridad);
        Cotizacion recomendada = estrategia.seleccionar(cotizaciones);

        List<Cotizacion> alternativas = cotizaciones.stream()
                .filter(c -> !c.nombreProveedor().equals(recomendada.nombreProveedor()))
                .toList();

        return new Recomendacion(recomendada, alternativas);
    }
}
