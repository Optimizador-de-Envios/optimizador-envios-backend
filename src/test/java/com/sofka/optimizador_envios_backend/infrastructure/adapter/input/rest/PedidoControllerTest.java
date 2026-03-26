package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.config.exception.GlobalExceptionHandler;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.OrderDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.UbicacionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private ObtenerRecomendacionUseCase obtenerRecomendacionUseCase;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PedidoController controller = new PedidoController(obtenerRecomendacionUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── helpers ─────────────────────────────────────────

    private PedidoRequestDto buildRequest(Double weight, String weightUnit, String priority) {
        UbicacionDto origin      = new UbicacionDto("Tunja, BY, Colombia",   5.53528,  -73.36778);
        UbicacionDto destination = new UbicacionDto("Bogotá, DC, Colombia",  4.635456, -74.08768);
        OrderDto order = new OrderDto(origin, destination, weight, weightUnit, priority);
        return new PedidoRequestDto(order);
    }

    // ─── happy path ──────────────────────────────────────

    @Test
    void dadoPedidoValido_cuandoPrioridadCost_entoncesRetorna200ConRecomendacionYAlternativas() throws Exception {
        Cotizacion recomendada = new Cotizacion("Local",  45000, 2);
        Cotizacion alt1        = new Cotizacion("FedEx",  55000, 1);
        Cotizacion alt2        = new Cotizacion("DHL",    60000, 1);

        when(obtenerRecomendacionUseCase.obtenerRecomendacion(any()))
                .thenReturn(new Recomendacion(recomendada, List.of(alt1, alt2)));

        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(5.0, "KILOGRAMS", "COST"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendation.providerName").value("Local"))
                .andExpect(jsonPath("$.recommendation.cost").value(45000))
                .andExpect(jsonPath("$.recommendation.currency").value("COP"))
                .andExpect(jsonPath("$.recommendation.estimatedDays").value(2))
                .andExpect(jsonPath("$.alternatives.length()").value(2));
    }

    @Test
    void dadoPedidoValido_cuandoPrioridadTime_entoncesRetorna200ConRecomendacionConMenorTiempo() throws Exception {
        Cotizacion recomendada = new Cotizacion("DHL",   60000, 1);
        Cotizacion alt1        = new Cotizacion("FedEx", 55000, 1);
        Cotizacion alt2        = new Cotizacion("Local", 45000, 2);

        when(obtenerRecomendacionUseCase.obtenerRecomendacion(any()))
                .thenReturn(new Recomendacion(recomendada, List.of(alt1, alt2)));

        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(5.0, "KILOGRAMS", "TIME"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendation.providerName").value("DHL"))
                .andExpect(jsonPath("$.recommendation.estimatedDays").value(1));
    }

    // ─── validaciones ────────────────────────────────────

    @Test
    void dadoPedidoConPesoCero_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(0.0, "KILOGRAMS", "COST"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dadoPedidoConPesoNulo_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(null, "KILOGRAMS", "COST"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dadoPedidoConPesoMayorA70Kg_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest(70.001, "KILOGRAMS", "COST"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dadoPedidoConOrigenNulo_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        OrderDto order = new OrderDto(
                null,
                new UbicacionDto("Bogotá, DC, Colombia", 4.635456, -74.08768),
                5.0, "KILOGRAMS", "COST"
        );

        PedidoRequestDto request = new PedidoRequestDto(order);

        mockMvc.perform(post("/api/v1/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}