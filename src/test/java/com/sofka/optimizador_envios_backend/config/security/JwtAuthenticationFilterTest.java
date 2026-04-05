package com.sofka.optimizador_envios_backend.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter("test-secret", "user-service");
    }

    @Test
    void dadoAuthorizationHeaderAusente_cuandoSeFiltraPedidoProtegido_entoncesDebeResponder401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/pedido/mis-pedidos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
    }

    @Test
    void dadoJwtValido_cuandoSeFiltraPedidoProtegido_entoncesDebeExponerElUserIdEnLaRequest() throws Exception {
        String token = buildToken("test-secret", "user-service", "user-123", "juan@example.com", Instant.now().plusSeconds(3600).getEpochSecond());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/pedido/mis-pedidos");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        assertEquals("user-123", request.getAttribute(JwtAuthenticationFilter.AUTHENTICATED_USER_ID_ATTRIBUTE));
        assertEquals("", response.getContentAsString());
    }

    private String buildToken(String secret, String issuer, String subject, String email, long exp) throws Exception {
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = base64Url(("{\"iss\":\"" + issuer + "\",\"sub\":\"" + subject + "\",\"email\":\"" + email + "\",\"iat\":" + Instant.now().getEpochSecond() + ",\"exp\":" + exp + "}").getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;
        String signature = base64Url(hmacSha256(secret.getBytes(StandardCharsets.UTF_8), signingInput.getBytes(StandardCharsets.UTF_8)));
        return signingInput + "." + signature;
    }

    private byte[] hmacSha256(byte[] secret, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
