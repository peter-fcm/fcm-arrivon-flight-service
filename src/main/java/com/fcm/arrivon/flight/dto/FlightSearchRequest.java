package com.fcm.arrivon.flight.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Data transfer record representing flight search parameters.
 */
public record FlightSearchRequest(
        @NotBlank(message = "Origin location code is required")
        @Size(min = 3, max = 3, message = "Origin location code must be 3-letter IATA code")
        String originLocationCode,

        @NotBlank(message = "Destination location code is required")
        @Size(min = 3, max = 3, message = "Destination location code must be 3-letter IATA code")
        String destinationLocationCode,

        @NotNull(message = "Departure date is required")
        LocalDate departureDate,

        LocalDate returnDate,

        @NotNull(message = "Adults count is required")
        @Min(value = 1, message = "At least one adult traveler is required")
        Integer adultsCount,

        Integer childrenCount,

        Integer infantsCount,

        String travelClass,

        String currencyCode,

        Integer maximumPrice,

        Integer maximumResults
) {
}
