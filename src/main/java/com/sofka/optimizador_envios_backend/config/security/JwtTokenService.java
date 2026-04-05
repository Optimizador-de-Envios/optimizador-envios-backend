package com.sofka.optimizador_envios_backend.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtTokenService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final byte[] jwtSecret;
    private final String jwtIssuer;

    public JwtTokenService(
            @Value("${app.security.jwt-secret}") String jwtSecret,
            @Value("${app.security.jwt-issuer}") String jwtIssuer
    ) {
        this.jwtSecret = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.jwtIssuer = jwtIssuer;
    }

    public String extractUserId(String token) {
        try {
            return verifyAndExtractUserId(token);
        } catch (IOException | GeneralSecurityException | IllegalArgumentException exception) {
            throw new JwtAuthenticationException("Token invalido", exception);
        }
    }

    private String verifyAndExtractUserId(String token) throws IOException, GeneralSecurityException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new GeneralSecurityException("Token invalido");
        }

        String signingInput = parts[0] + "." + parts[1];
        byte[] expectedSignature = hmacSha256(signingInput.getBytes(StandardCharsets.UTF_8));
        byte[] actualSignature = Base64.getUrlDecoder().decode(parts[2]);

        if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
            throw new GeneralSecurityException("Firma invalida");
        }

        JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
        String issuer = payload.path("iss").asText(null);
        if (!jwtIssuer.equals(issuer)) {
            throw new GeneralSecurityException("Issuer invalido");
        }

        long expiration = payload.path("exp").asLong(0L);
        if (expiration > 0 && System.currentTimeMillis() / 1000L >= expiration) {
            throw new GeneralSecurityException("Token expirado");
        }

        String userId = payload.path("sub").asText(null);
        if (userId == null || userId.isBlank()) {
            throw new GeneralSecurityException("Subject invalido");
        }

        return userId;
    }

    private byte[] hmacSha256(byte[] data) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(jwtSecret, "HmacSHA256"));
        return mac.doFinal(data);
    }
}