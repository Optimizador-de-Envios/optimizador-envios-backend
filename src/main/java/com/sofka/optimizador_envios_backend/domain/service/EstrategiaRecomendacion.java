package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;

import java.util.List;

public interface EstrategiaRecomendacion {
    Cotizacion seleccionar(List<Cotizacion> cotizaciones);
}
