package org.RRA.tax_appeal_system.DTOS.responses;

import jakarta.validation.constraints.NotBlank;

public record AuthResponse(
        @NotBlank(message = "full names required")
        String fullNames,
        @NotBlank(message = "email required")
        String email,
        @NotBlank(message = "role required")
        String role,
        String accessToken,
        String refreshToken
) {
}
