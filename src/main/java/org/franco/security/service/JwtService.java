package org.franco.security.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.franco.common.exception.UnauthorizedException;
import org.franco.security.entity.AppUser;

@ApplicationScoped
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String secret;
    private final String issuer;
    private final long tokenTtlSeconds;

    public JwtService(
            ObjectMapper objectMapper,
            @ConfigProperty(name = "app.security.jwt-secret") String secret,
            @ConfigProperty(name = "app.security.jwt-issuer") String issuer,
            @ConfigProperty(name = "app.security.token-ttl-seconds") long tokenTtlSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.issuer = issuer;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public Token createToken(AppUser user) {
        OffsetDateTime expiresAt = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(tokenTtlSeconds);
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = Map.of(
                "iss", issuer,
                "sub", user.email,
                "role", user.role.name(),
                "iat", OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond(),
                "exp", expiresAt.toEpochSecond());

        String unsigned = encode(header) + "." + encode(payload);
        String signature = sign(unsigned);
        return new Token(unsigned + "." + signature, expiresAt);
    }

    public Claims verify(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("Invalid token");
        }
        String unsigned = parts[0] + "." + parts[1];
        if (!MessageDigest.isEqual(sign(unsigned).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("Invalid token signature");
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {
                    });
            if (!issuer.equals(payload.get("iss"))) {
                throw new UnauthorizedException("Invalid token issuer");
            }
            long exp = ((Number) payload.get("exp")).longValue();
            if (OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond() >= exp) {
                throw new UnauthorizedException("Token expired");
            }
            return new Claims((String) payload.get("sub"), (String) payload.get("role"));
        } catch (UnauthorizedException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    private String encode(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to encode JWT", exception);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }

    public record Token(String value, OffsetDateTime expiresAt) {
    }

    public record Claims(String subject, String role) {
    }
}
