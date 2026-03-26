package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.sofka.optimizador_envios_backend.config.ProveedorConfig;

public class LocalClientMock extends BaseProveedorClientMock {

    public LocalClientMock(ProveedorConfig config) {
        super(config);
    }

    @Override
    protected String nombreProveedor() {
        return "Local";
    }
}
