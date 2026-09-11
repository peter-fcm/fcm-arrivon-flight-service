package com.fcm.arrivon.flight.dto;

import java.time.LocalDate;

/**
 * Data transfer record representing an identification document for travel.
 */
public record TravelerDocument(
        String documentType,
        String documentNumber,
        String issuanceCountry,
        LocalDate expiryDate,
        String nationality,
        Boolean holder
) {
}
