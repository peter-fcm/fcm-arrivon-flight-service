package com.fcm.arrivon.flight.dto;

import java.util.List;

/**
 * Data transfer record summarizing a bookable flight offer.
 */
public record FlightOfferSummary(
        String offerId,
        String source,
        Integer numberOfBookableSeats,
        Boolean oneWay,
        List<FlightItinerarySummary> itineraries,
        FlightPriceSummary price,
        List<String> validatingAirlineCodes
) {
    public FlightOfferSummary {
        itineraries = (itineraries == null) ? List.of() : List.copyOf(itineraries);
        validatingAirlineCodes = (validatingAirlineCodes == null) ? List.of() : List.copyOf(validatingAirlineCodes);
    }
}
