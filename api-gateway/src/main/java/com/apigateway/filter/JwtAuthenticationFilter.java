package com.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Global JWT authentication & authorization filter for the API Gateway.
 *
 * NOTE:
 * - Requests will typically look like:
 *   - http://localhost:8080/doctor/api/v1/doctor/search
 *   - http://localhost:8080/patient/api/v1/patient/getpatientbyid
 *   - http://localhost:8080/booking/api/v1/booking/...
 * - Route predicates (in application.properties) should match these prefixes.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // TODO: move to config / env variable in real setup
    private static final String SECRET_KEY = "secret12345";

    /**
     * Public endpoints that don't require authentication.
     * These paths are from the API Gateway perspective (before StripPrefix).
     */
    private static final List<String> openApiEndpoints = List.of(
            // Auth service (login & signup)
            "/auth/api/v1/auth/login",
            "/auth/api/v1/auth/signup",

            // Doctor service - search and read-only operations
            "/doctor/api/v1/doctor/search",
            "/doctor/api/v1/doctor/getdoctorbyid",

            // Patient service - basic fetch (optional)
            "/patient/api/v1/patient/getpatientbyid"
    );

    /**
     * Map of protected path prefixes to allowed roles.
     * Example:
     * - All booking APIs require ROLE_USER or ROLE_ADMIN.
     */
    private static final Map<String, List<String>> protectedEndpointsWithRoles = Map.of(
            "/booking/api/v1/booking", List.of("ROLE_USER", "ROLE_ADMIN")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        // Allow public endpoints
        if (isPublicEndpoint(requestPath)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);

            String role = jwt.getClaim("role").asString();

            System.out.println("Request path: " + requestPath);
            System.out.println("Role from token: " + role);

            if (!isAuthorized(requestPath, role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Optionally pass role (or other claims) downstream to services
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Role", role))
                    .build();

        } catch (JWTVerificationException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return openApiEndpoints.stream().anyMatch(path::equalsIgnoreCase);
    }

    private boolean isAuthorized(String path, String role) {
        for (Map.Entry<String, List<String>> entry : protectedEndpointsWithRoles.entrySet()) {
            String protectedPath = entry.getKey();
            List<String> allowedRoles = entry.getValue();

            if (path.startsWith(protectedPath)) {
                System.out.println("Matched protected path: " + protectedPath + " | Allowed roles: " + allowedRoles);
                return allowedRoles.contains(role);
            }
        }
        // If path is not explicitly protected, allow by default
        return true;
    }

    @Override
    public int getOrder() {
        // Run early in the filter chain
        return -1;
    }
}


