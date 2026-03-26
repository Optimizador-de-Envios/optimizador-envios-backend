package com.sofka.optimizador_envios_backend.domain.service;

import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;

public class EstrategiaFactory {

    public EstrategiaRecomendacion obtener(Prioridad prioridad) {
        return switch (prioridad) {
            case COST -> new EstrategiaCosto();
            case TIME -> new EstrategiaTiempo();
        };
    }
}
