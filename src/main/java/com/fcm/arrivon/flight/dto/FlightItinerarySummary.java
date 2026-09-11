package com.fcm.arrivon.flight.dto;

import java.util.List;

/**
 * Data transfer record representing a flight itinerary composed of one or more segments.
 */
public record FlightItinerarySummary(
        String duration,
        List<FlightSegmentSummary> segments
) {
    public FlightItinerarySummary {
        segments = (segments == null) ? List.of() : List.copyOf(segments);
    }
}
