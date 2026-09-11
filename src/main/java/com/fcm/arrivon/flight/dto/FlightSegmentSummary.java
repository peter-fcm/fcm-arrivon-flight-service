package com.fcm.arrivon.flight.dto;

/**
 * Data transfer record representing a single flight segment within an itinerary.
 */
public record FlightSegmentSummary(
        String departureAirportCode,
        String departureTime,
        String arrivalAirportCode,
        String arrivalTime,
        String carrierCode,
        String flightNumber,
        String aircraftCode,
        String duration,
        Integer numberOfStops
) {
}
