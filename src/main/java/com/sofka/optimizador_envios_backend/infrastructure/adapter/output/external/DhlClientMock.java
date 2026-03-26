package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.sofka.optimizador_envios_backend.config.ProveedorConfig;

public class DhlClientMock extends BaseProveedorClientMock {

    public DhlClientMock(ProveedorConfig config) {
        super(config);
    }

    @Override
    protected String nombreProveedor() {
        return "DHL";
    }
}
