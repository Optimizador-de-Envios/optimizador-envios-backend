package com.sofka.optimizador_envios_backend.domain.model;

public record ConfirmacionPedido(
        String id,
        Pedido pedido,
        double distanciaKm,
        Cotizacion opcionSeleccionada
) {}