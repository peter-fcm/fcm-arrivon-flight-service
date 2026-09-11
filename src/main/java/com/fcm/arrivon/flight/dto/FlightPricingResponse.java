package com.fcm.arrivon.flight.dto;

import java.util.List;

/**
 * Data transfer record returning validated pricing and fare conditions for flight offer(s).
 */
public record FlightPricingResponse(
        String pricingType,
        List<FlightOfferSummary> flightOffers
) {
    public FlightPricingResponse {
        flightOffers = (flightOffers == null) ? List.of() : List.copyOf(flightOffers);
    }
}
