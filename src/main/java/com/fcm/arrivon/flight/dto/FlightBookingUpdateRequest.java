package com.fcm.arrivon.flight.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer record for updating remarks or details on an existing flight booking order.
 */
public record FlightBookingUpdateRequest(
        @NotBlank(message = "Updated remarks must not be blank")
        String remarks
) {
}
