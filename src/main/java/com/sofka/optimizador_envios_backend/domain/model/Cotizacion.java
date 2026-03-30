package com.sofka.optimizador_envios_backend.domain.model;

import java.util.Objects;

public record Cotizacion(
        String nombreProveedor,
        double costo,
        String moneda,
        int diasEntrega
) {

    public boolean coincideCon(Cotizacion otra) {
        return otra != null
                && Objects.equals(nombreProveedor, otra.nombreProveedor())
                && Double.compare(costo, otra.costo()) == 0
                && Objects.equals(moneda, otra.moneda())
                && diasEntrega == otra.diasEntrega();
    }
}
