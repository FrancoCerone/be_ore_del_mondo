package org.franco.security;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.security.Principal;
import org.franco.common.dto.ErrorResponse;
import org.franco.common.exception.UnauthorizedException;
import org.franco.security.service.JwtService;

@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String ADMIN_PATH_PREFIX = "api/admin";
    private static final String AUTH_ME_PATH = "api/auth/me";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }
        String path = fullRequestPath(requestContext);
        if (!requiresAuthentication(path)) {
            return;
        }

        String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing bearer token");
            return;
        }

        try {
            JwtService.Claims claims = jwtService.verify(authorization.substring("Bearer ".length()));
            if (!"ADMIN".equals(claims.role())) {
                abort(requestContext, Response.Status.FORBIDDEN, "Admin role required");
                return;
            }
            requestContext.setSecurityContext(new JwtSecurityContext(claims.subject(), claims.role(), requestContext.getSecurityContext().isSecure()));
        } catch (UnauthorizedException exception) {
            abort(requestContext, Response.Status.UNAUTHORIZED, exception.getMessage());
        }
    }

    /**
     * UriInfo#getPath() is often relative to the matched JAX-RS resource (e.g. only {@code me}
     * for {@code /api/auth/me}), so admin routes would never match. Use the path from the HTTP
     * request URI instead.
     */
    private static String fullRequestPath(ContainerRequestContext requestContext) {
        String raw = requestContext.getUriInfo().getRequestUri().getPath();
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        return raw.startsWith("/") ? raw.substring(1) : raw;
    }

    private boolean requiresAuthentication(String path) {
        return path.startsWith(ADMIN_PATH_PREFIX) || AUTH_ME_PATH.equals(path);
    }

    private void abort(ContainerRequestContext context, Response.Status status, String message) {
        context.abortWith(Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ErrorResponse.of(status.name(), message, context.getUriInfo().getPath()))
                .build());
    }

    private record JwtSecurityContext(String subject, String role, boolean secure) implements jakarta.ws.rs.core.SecurityContext {
        @Override
        public Principal getUserPrincipal() {
            return () -> subject;
        }

        @Override
        public boolean isUserInRole(String requestedRole) {
            return role.equals(requestedRole);
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
