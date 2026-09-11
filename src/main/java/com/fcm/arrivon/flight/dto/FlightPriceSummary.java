package com.fcm.arrivon.flight.dto;

/**
 * Data transfer record summarizing flight pricing information.
 */
public record FlightPriceSummary(
        String currency,
        String total,
        String base,
        String grandTotal
) {
}
