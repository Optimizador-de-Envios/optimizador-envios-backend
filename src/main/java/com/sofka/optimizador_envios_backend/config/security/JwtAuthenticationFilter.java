package com.sofka.optimizador_envios_backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_USER_ID_ATTRIBUTE = "authenticatedUserId";
    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || request.getRequestURI() == null
                || !isProtectedPedidoEndpoint(request.getRequestURI());
    }

    private boolean isProtectedPedidoEndpoint(String requestUri) {
        return "/api/v1/pedido/confirmar".equals(requestUri)
                || "/api/v1/pedido/mis-pedidos".equals(requestUri);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
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
            String userId = jwtTokenService.extractUserId(token);
            request.setAttribute(AUTHENTICATED_USER_ID_ATTRIBUTE, userId);
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException exception) {
            writeUnauthorized(response);
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        String message = "Token ausente o invalido";
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Vary", "Origin");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\",\"errors\":[\"" + message + "\"]}");
    }
}
