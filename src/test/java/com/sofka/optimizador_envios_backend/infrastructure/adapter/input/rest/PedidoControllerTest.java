package com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.optimizador_envios_backend.application.port.input.ConfirmarPedidoUseCase;
import com.sofka.optimizador_envios_backend.application.port.input.ObtenerRecomendacionUseCase;
import com.sofka.optimizador_envios_backend.config.exception.GlobalExceptionHandler;
import com.sofka.optimizador_envios_backend.domain.exception.PedidoInvalidoException;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.domain.model.Cotizacion;
import com.sofka.optimizador_envios_backend.domain.model.Pedido;
import com.sofka.optimizador_envios_backend.domain.model.Recomendacion;
import com.sofka.optimizador_envios_backend.domain.model.Ubicacion;
import com.sofka.optimizador_envios_backend.domain.valueobject.ConfirmationToken;
import com.sofka.optimizador_envios_backend.domain.valueobject.Prioridad;
import com.sofka.optimizador_envios_backend.domain.valueobject.UnidadPeso;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.ConfirmacionPedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.OrderDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.PedidoRequestDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.SelectedOptionDto;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.input.rest.dto.UbicacionDto;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.PedidoMapper;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private ObtenerRecomendacionUseCase obtenerRecomendacionUseCase;

        @Mock
        private ConfirmarPedidoUseCase confirmarPedidoUseCase;

        @Mock
        private com.sofka.optimizador_envios_backend.application.port.input.ObtenerMisPedidosUseCase obtenerMisPedidosUseCase;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

                @SuppressWarnings("unused")
                @BeforeEach
    void setUp() {
        PedidoController controller = new PedidoController(
                obtenerRecomendacionUseCase,
                confirmarPedidoUseCase,
                obtenerMisPedidosUseCase,
                new PedidoMapper()
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .defaultRequest(get("/").requestAttr("authenticatedUserId", "user-123"))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── helpers ─────────────────────────────────────────

    private PedidoRequestDto buildRequest(Double weight, String weightUnit, String priority) {
                UbicacionDto origin = new UbicacionDto("Tunja, BY, Colombia", 5.53528, -73.36778);
        UbicacionDto destination = new UbicacionDto("Bogotá, DC, Colombia",  4.635456, -74.08768);
        OrderDto order = new OrderDto(origin, destination, weight, weightUnit, priority);
        return new PedidoRequestDto(order);
    }

        private ConfirmacionPedidoRequestDto buildConfirmRequest(String confirmationToken, Double weight, String weightUnit, String priority,
                                                                 String providerName, Double cost, String currency,
                                                                 Integer estimatedDays) {
                UbicacionDto origin = new UbicacionDto("Tunja, BY, Colombia", 5.53528, -73.36778);
                UbicacionDto destination = new UbicacionDto("Bogotá, DC, Colombia", 4.635456, -74.08768);
                OrderDto order = new OrderDto(origin, destination, weight, weightUnit, priority);
                SelectedOptionDto selectedOption = new SelectedOptionDto(providerName, cost, currency, estimatedDays);
                return new ConfirmacionPedidoRequestDto(confirmationToken, order, selectedOption);
        }

        private Pedido buildConfirmedPedido() {
                return new Pedido(
                                new Ubicacion("Tunja, BY, Colombia", 5.53528, -73.36778),
                                new Ubicacion("Bogotá, DC, Colombia", 4.635456, -74.08768),
                                10.0,
                                UnidadPeso.KILOGRAMS,
                                Prioridad.COST
                );
        }

    // ─── happy path ──────────────────────────────────────

    @Test
    void dadoPedidoValido_cuandoPrioridadCost_entoncesRetorna200ConRecomendacionYAlternativas() throws Exception {
        Cotizacion recomendada = new Cotizacion("Local",  45000, "COP", 2);
        Cotizacion alt1        = new Cotizacion("FedEx",  55000, "COP", 1);
        Cotizacion alt2        = new Cotizacion("DHL",    60000, "COP", 1);

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
        Cotizacion recomendada = new Cotizacion("DHL",   60000, "COP", 1);
        Cotizacion alt1        = new Cotizacion("FedEx", 55000, "COP", 1);
        Cotizacion alt2        = new Cotizacion("Local", 45000, "COP", 2);

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

    @Test
    void dadoPedidoConfirmadoValido_cuandoSeConfirmaProveedor_entoncesRetorna201ConLaConfirmacionGuardada() throws Exception {
        Pedido pedido = buildConfirmedPedido();
        Cotizacion seleccionada = new Cotizacion("Local", 30386.59, "COP", 1);
        ConfirmacionPedido confirmacion = new ConfirmacionPedido(
                "abc-123",
                "user-123",
                ConfirmationToken.of("token-123"),
                pedido,
                148.3,
                seleccionada,
                Instant.parse("2026-04-03T18:35:00Z")
        );

        when(confirmarPedidoUseCase.confirmar(eq("user-123"), eq("token-123"), any(), any())).thenReturn(confirmacion);

        mockMvc.perform(post("/api/v1/pedido/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildConfirmRequest("token-123", 10.0, "KILOGRAMS", "COST", "Local", 30386.59, "COP", 1)
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.confirmationToken").value("token-123"))
                .andExpect(jsonPath("$.origin.name").value("Tunja, BY, Colombia"))
                .andExpect(jsonPath("$.destination.name").value("Bogotá, DC, Colombia"))
                .andExpect(jsonPath("$.weight").value(10.0))
                .andExpect(jsonPath("$.weightUnit").value("KILOGRAMS"))
                .andExpect(jsonPath("$.priority").value("COST"))
                .andExpect(jsonPath("$.distanceKm").value(148.3))
                .andExpect(jsonPath("$.selectedOption.providerName").value("Local"))
                .andExpect(jsonPath("$.selectedOption.cost").value(30386.59))
                .andExpect(jsonPath("$.selectedOption.currency").value("COP"))
                .andExpect(jsonPath("$.selectedOption.estimatedDays").value(1))
                .andExpect(jsonPath("$.createdAt").value("2026-04-03T18:35:00Z"));
    }

    @Test
    void dadoConfirmacionSinProveedorSeleccionado_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        ConfirmacionPedidoRequestDto request = new ConfirmacionPedidoRequestDto(
                "token-123",
                new OrderDto(
                        new UbicacionDto("Tunja, BY, Colombia", 5.53528, -73.36778),
                        new UbicacionDto("Bogotá, DC, Colombia", 4.635456, -74.08768),
                        10.0,
                        "KILOGRAMS",
                        "COST"
                ),
                null
        );

        mockMvc.perform(post("/api/v1/pedido/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dadoConfirmacionSinConfirmationToken_cuandoSeEnvia_entoncesRetorna400() throws Exception {
        ConfirmacionPedidoRequestDto request = new ConfirmacionPedidoRequestDto(
                " ",
                new OrderDto(
                        new UbicacionDto("Tunja, BY, Colombia", 5.53528, -73.36778),
                        new UbicacionDto("Bogotá, DC, Colombia", 4.635456, -74.08768),
                        10.0,
                        "KILOGRAMS",
                        "COST"
                ),
                new SelectedOptionDto("Local", 30386.59, "COP", 1)
        );

        mockMvc.perform(post("/api/v1/pedido/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dadoConfirmacionConOpcionInvalida_cuandoSeEnvia_entoncesRetorna400ConMensajeDeNegocio() throws Exception {
                when(confirmarPedidoUseCase.confirmar(any(), any(), any(), any()))
                .thenThrow(new PedidoInvalidoException("La opcion seleccionada no coincide con las cotizaciones disponibles"));

        mockMvc.perform(post("/api/v1/pedido/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildConfirmRequest("token-123", 10.0, "KILOGRAMS", "COST", "Local", 99999.0, "COP", 1)
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La opcion seleccionada no coincide con las cotizaciones disponibles"));
    }

    @Test
    void dadoUsuarioAutenticadoConPedidos_cuandoConsultaSuHistorial_entoncesRetorna200ConSoloSusPedidos() throws Exception {
        ConfirmacionPedido confirmacionUno = new ConfirmacionPedido(
                "abc-123",
                "user-123",
                ConfirmationToken.of("token-123"),
                buildConfirmedPedido(),
                148.3,
                new Cotizacion("Local", 30386.59, "COP", 1),
                Instant.parse("2026-04-03T18:35:00Z")
        );
        ConfirmacionPedido confirmacionDos = new ConfirmacionPedido(
                "def-456",
                "user-123",
                ConfirmationToken.of("token-456"),
                new Pedido(
                        new Ubicacion("Medellin, ANT, Colombia", 6.2442, -75.5812),
                        new Ubicacion("Cali, VAC, Colombia", 3.4516, -76.5320),
                        5.0,
                        UnidadPeso.KILOGRAMS,
                        Prioridad.TIME
                ),
                298.1,
                new Cotizacion("DHL", 35500.0, "COP", 1),
                Instant.parse("2026-04-03T19:00:00Z")
        );

        when(obtenerMisPedidosUseCase.obtenerMisPedidos("user-123")).thenReturn(List.of(confirmacionUno, confirmacionDos));

        mockMvc.perform(get("/api/v1/pedido/mis-pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("abc-123"))
                .andExpect(jsonPath("$[0].confirmationToken").doesNotExist())
                .andExpect(jsonPath("$[0].selectedOption.providerName").value("Local"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-04-03T18:35:00Z"))
                .andExpect(jsonPath("$[1].id").value("def-456"))
                .andExpect(jsonPath("$[1].selectedOption.providerName").value("DHL"))
                .andExpect(jsonPath("$[1].createdAt").value("2026-04-03T19:00:00Z"));
    }
}