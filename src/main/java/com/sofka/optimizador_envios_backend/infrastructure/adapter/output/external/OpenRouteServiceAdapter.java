package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.optimizador_envios_backend.application.port.output.DistanciaClient;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenRouteServiceAdapter implements DistanciaClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenRouteServiceAdapter(
            RestTemplate restTemplate,
            @Value("${openrouteservice.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    public double obtenerDistanciaKm(Ubicacion origen, Ubicacion destino) {
        String url = String.format(
                "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%s,%s&end=%s,%s",
                apiKey,
                origen.lng(), origen.lat(),
                destino.lng(), destino.lat()
        );

        String response = restTemplate.getForObject(url, String.class);
        return parsearDistanciaKm(response);
    }

    private double parsearDistanciaKm(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            double distanciaMetros = root.at("/features/0/properties/summary/distance").asDouble();
            return distanciaMetros / 1000.0;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta de OpenRouteService", e);
        }
    }
}
