package com.sofka.optimizador_envios_backend.config;

import com.sofka.optimizador_envios_backend.domain.service.EstrategiaFactory;
import com.sofka.optimizador_envios_backend.domain.service.MotorRecomendacionService;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external.DhlClientMock;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external.FedexClientMock;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external.LocalClientMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public EstrategiaFactory estrategiaFactory() {
        return new EstrategiaFactory();
    }

    @Bean
    public MotorRecomendacionService motorRecomendacionService(EstrategiaFactory estrategiaFactory) {
        return new MotorRecomendacionService(estrategiaFactory);
    }

    @Bean
    public FedexClientMock fedexClient(ProveedorProperties properties) {
        return new FedexClientMock(properties.getFedex());
    }

    @Bean
    public DhlClientMock dhlClient(ProveedorProperties properties) {
        return new DhlClientMock(properties.getDhl());
    }

    @Bean
    public LocalClientMock localClient(ProveedorProperties properties) {
        return new LocalClientMock(properties.getLocal());
    }
}
