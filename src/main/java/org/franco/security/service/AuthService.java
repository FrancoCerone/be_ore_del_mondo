package org.franco.security.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.franco.common.exception.ConflictException;
import org.franco.common.exception.UnauthorizedException;
import org.franco.security.dto.AuthResponse;
import org.franco.security.dto.LoginRequest;
import org.franco.security.dto.RegisterRequest;
import org.franco.security.entity.AppUser;
import org.franco.security.entity.UserRole;
import org.franco.security.repository.UserRepository;

@ApplicationScoped
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.count() > 0) {
            throw new ConflictException("Admin registration is already completed");
        }
        String email = request.email().toLowerCase();
        if (userRepository.emailExists(email)) {
            throw new ConflictException("Email already registered");
        }
        AppUser user = new AppUser();
        user.email = email;
        user.passwordHash = BcryptUtil.bcryptHash(request.password());
        user.role = UserRole.ADMIN;
        user.enabled = true;
        userRepository.persist(user);
        return tokenResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.email())
                .filter(candidate -> Boolean.TRUE.equals(candidate.enabled))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!BcryptUtil.matches(request.password(), user.passwordHash)) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return tokenResponse(user);
    }

    private AuthResponse tokenResponse(AppUser user) {
        JwtService.Token token = jwtService.createToken(user);
        return new AuthResponse("Bearer", token.value(), token.expiresAt());
    }
}
