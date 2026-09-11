package com.fcm.arrivon.flight.dto;

import java.util.List;

/**
 * Data transfer record representing the result of a flight offers search.
 */
public record FlightSearchResponse(
        Integer totalOffers,
        List<FlightOfferSummary> flightOffers
) {
    public FlightSearchResponse {
        flightOffers = (flightOffers == null) ? List.of() : List.copyOf(flightOffers);
    }
}
