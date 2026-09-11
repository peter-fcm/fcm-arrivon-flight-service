package com.fcm.arrivon.flight.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer record representing a traveler's name.
 */
public record TravelerName(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {
}
