package com.sofka.optimizador_envios_backend.config.security;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenServiceTest {

    private final JwtTokenService jwtTokenService = new JwtTokenService("test-secret", "user-service");

    @Test
    void dadoJwtValido_cuandoSeExtraeElUserId_entoncesRetornaElSubject() throws Exception {
        String token = buildToken("test-secret", "user-service", "user-123", "juan@example.com", Instant.now().plusSeconds(3600).getEpochSecond());

        String userId = jwtTokenService.extractUserId(token);

        assertEquals("user-123", userId);
    }

    @Test
    void dadoJwtConFirmaInvalida_cuandoSeExtraeElUserId_entoncesLanzaExcepcionDeAutenticacion() throws Exception {
        String token = buildToken("other-secret", "user-service", "user-123", "juan@example.com", Instant.now().plusSeconds(3600).getEpochSecond());

        JwtAuthenticationException exception = assertThrows(JwtAuthenticationException.class, () -> jwtTokenService.extractUserId(token));

        assertEquals("Token invalido", exception.getMessage());
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