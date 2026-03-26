package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.sofka.optimizador_envios_backend.application.port.output.ProveedorClient;
import com.sofka.optimizador_envios_backend.config.ProveedorConfig;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;

public class FedexClientMock implements ProveedorClient {

    private final ProveedorConfig config;

    public FedexClientMock(ProveedorConfig config) {
        this.config = config;
    }

    @Override
    public Cotizacion cotizar(Pedido pedido, double distanciaKm) {
        double pesoKg = convertirAKg(pedido.peso(), pedido.unidadPeso());
        double costo = config.getCostoBase()
                + (pesoKg * config.getPrecioPorKg())
                + (distanciaKm * config.getPrecioPorKm());
        int dias = (int) Math.ceil(distanciaKm / config.getKmPorDia());
        return new Cotizacion("FedEx", costo, dias);
    }

    private double convertirAKg(double peso, UnidadPeso unidad) {
        return switch (unidad) {
            case KILOGRAMS -> peso;
            case GRAMS -> peso / 1000.0;
            case POUNDS -> peso * 0.453592;
        };
    }
}
