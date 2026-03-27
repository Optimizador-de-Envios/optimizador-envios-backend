package com.sofka.optimizador_envios_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "proveedores")
public class ProveedorProperties {

    private ProveedorConfig fedex = new ProveedorConfig();
    private ProveedorConfig dhl   = new ProveedorConfig();
    private ProveedorConfig local = new ProveedorConfig();

    public ProveedorConfig getFedex() { return fedex; }
    public void setFedex(ProveedorConfig fedex) { this.fedex = fedex; }

    public ProveedorConfig getDhl() { return dhl; }
    public void setDhl(ProveedorConfig dhl) { this.dhl = dhl; }

    public ProveedorConfig getLocal() { return local; }
    public void setLocal(ProveedorConfig local) { this.local = local; }
}
