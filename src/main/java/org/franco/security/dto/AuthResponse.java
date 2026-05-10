package org.franco.security.dto;

import java.time.OffsetDateTime;

public record AuthResponse(
        String tokenType,
        String accessToken,
        OffsetDateTime expiresAt) {
}
