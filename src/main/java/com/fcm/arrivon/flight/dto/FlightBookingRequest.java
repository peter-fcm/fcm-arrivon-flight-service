package com.fcm.arrivon.flight.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Data transfer record for creating a flight order / booking.
 */
public record FlightBookingRequest(
        @NotEmpty(message = "Flight offers must not be empty")
        List<FlightOfferSummary> flightOffers,

        @NotEmpty(message = "Travelers must not be empty")
        List<@Valid TravelerDetails> travelers,

        String remarks
) {
    public FlightBookingRequest {
        flightOffers = (flightOffers == null) ? List.of() : List.copyOf(flightOffers);
        travelers = (travelers == null) ? List.of() : List.copyOf(travelers);
    }
}
