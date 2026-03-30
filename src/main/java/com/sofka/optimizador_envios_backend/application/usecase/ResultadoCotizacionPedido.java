package com.sofka.optimizador_envios_backend.application.usecase;

import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;

import java.util.List;

public record ResultadoCotizacionPedido(
        double distanciaKm,
        List<Cotizacion> cotizaciones
) {}