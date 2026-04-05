package com.sofka.optimizador_envios_backend.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_USER_ID_ATTRIBUTE = "authenticatedUserId";

    private final ObjectMapper objectMapper;
    private final byte[] jwtSecret;
    private final String jwtIssuer;

    public JwtAuthenticationFilter(
            @Value("${app.security.jwt-secret}") String jwtSecret,
            @Value("${app.security.jwt-issuer}") String jwtIssuer
    ) {
        this.objectMapper = new ObjectMapper();
        this.jwtSecret = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI() == null || !request.getRequestURI().startsWith("/api/v1/pedido");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            writeUnauthorized(response);
            return;
        }

        try {
            String userId = verifyAndExtractUserId(token);

            if (userId == null || userId.isBlank()) {
                writeUnauthorized(response);
                return;
            }

            request.setAttribute(AUTHENTICATED_USER_ID_ATTRIBUTE, userId);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            writeUnauthorized(response);
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

        return payload.path("sub").asText(null);
    }

    private byte[] hmacSha256(byte[] data) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(jwtSecret, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        String message = "Token ausente o invalido";
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\",\"errors\":[\"" + message + "\"]}");
    }
}
