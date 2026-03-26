package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.external;

import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenRouteServiceAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenRouteServiceAdapter adapter;

    private final Ubicacion tunja  = new Ubicacion("Tunja, BY, Colombia",  5.53528,   -73.36778);
    private final Ubicacion bogota = new Ubicacion("Bogotá, DC, Colombia", 4.635456,  -74.08768);

    @BeforeEach
    void setUp() {
        adapter = new OpenRouteServiceAdapter(restTemplate, "test-api-key");
    }

    @Test
    void dadaUbicacionOrigenYDestino_cuandoSeObtieneLaDistancia_entoncesDebeRetornarDistanciaEnKm() {
        // ORS devuelve distance en METROS → 150600.0m = 150.6km
        String mockResponse = """
                {
                  "features": [{
                    "properties": {
                      "summary": {
                        "distance": 150600.0,
                        "duration": 5400.0
                      }
                    }
                  }]
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        double distanciaKm = adapter.obtenerDistanciaKm(tunja, bogota);

        assertEquals(150.6, distanciaKm, 0.01);
    }

    @Test
    void dadaUbicacionOrigenYDestino_cuandoSeConsulta_entoncesDebeLlamarConCoordenadaEnOrdenLngLat() {
        String mockResponse = """
                {
                  "features": [{
                    "properties": {
                      "summary": { "distance": 120000.0, "duration": 3600.0 }
                    }
                  }]
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        adapter.obtenerDistanciaKm(tunja, bogota);

        // Verifica que la URL contiene lng,lat (no lat,lng)
        // Tunja: lng=-73.36778   lat=5.53528  → start=-73.36778,5.53528
        // Bogotá: lng=-74.08768  lat=4.635456 → end=-74.08768,4.635456
        verify(restTemplate).getForObject(
                argThat(url -> url.contains("start=-73.36778,5.53528")
                             && url.contains("end=-74.08768,4.635456")),
                eq(String.class)
        );
    }

    @Test
    void dadaUbicacionOrigenYDestino_cuandoSeConsulta_entoncesDebeIncluirApiKeyEnLaUrl() {
        String mockResponse = """
                {
                  "features": [{
                    "properties": {
                      "summary": { "distance": 120000.0, "duration": 3600.0 }
                    }
                  }]
                }
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        adapter.obtenerDistanciaKm(tunja, bogota);

        verify(restTemplate).getForObject(
                argThat(url -> url.contains("api_key=test-api-key")),
                eq(String.class)
        );
    }
}
