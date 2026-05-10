package org.franco.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank @Size(max = 180) String email,
        @NotBlank @Size(min = 10, max = 120) String password) {
}
