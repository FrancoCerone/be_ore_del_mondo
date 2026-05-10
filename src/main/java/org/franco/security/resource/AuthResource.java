package org.franco.security.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import org.franco.common.exception.UnauthorizedException;
import org.franco.security.dto.AuthResponse;
import org.franco.security.dto.LoginRequest;
import org.franco.security.dto.RegisterRequest;
import org.franco.security.dto.UserInfo;
import org.franco.security.service.AuthService;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthResponse register(@Valid RegisterRequest request) {
        return authService.register(request);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthResponse login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @GET
    @Path("/me")
    public UserInfo me(@Context SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        String role = securityContext.isUserInRole("ADMIN") ? "ADMIN" : "USER";
        return new UserInfo(principal.getName(), role);
    }
}
