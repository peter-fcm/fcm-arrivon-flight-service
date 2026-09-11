package com.fcm.arrivon.flight.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Data transfer record requesting price confirmation and additional details for selected flight offer(s).
 */
public record FlightPricingRequest(
        @NotEmpty(message = "At least one flight offer is required for pricing")
        List<FlightOfferSummary> flightOffers,

        List<String> includeOtherServices
) {
    public FlightPricingRequest {
        flightOffers = (flightOffers == null) ? List.of() : List.copyOf(flightOffers);
        includeOtherServices = (includeOtherServices == null) ? List.of() : List.copyOf(includeOtherServices);
    }
}
