package com.fcm.arrivon.flight.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer record representing traveler contact details.
 */
public record TravelerContact(
        @NotBlank(message = "Email address is required")
        @Email(message = "Email address must be well-formed")
        String emailAddress,

        String phoneNumber
) {
}
