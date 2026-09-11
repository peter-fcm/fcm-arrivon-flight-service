package com.fcm.arrivon.flight.dto;

import java.util.List;

/**
 * Data transfer record representing a created or retrieved flight booking order.
 */
public record FlightBookingResponse(
        String flightOrderId,
        String reference,
        String creationDateTime,
        String bookingStatus,
        List<FlightOfferSummary> flightOffers,
        List<TravelerDetails> travelers,
        String remarks
) {
    public FlightBookingResponse {
        flightOffers = (flightOffers == null) ? List.of() : List.copyOf(flightOffers);
        travelers = (travelers == null) ? List.of() : List.copyOf(travelers);
    }
}
