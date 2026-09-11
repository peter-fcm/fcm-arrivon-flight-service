package com.fcm.arrivon.flight.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Data transfer record representing passenger/traveler information for booking.
 */
public record TravelerDetails(
        @NotBlank(message = "Traveler identifier is required")
        String travelerId,

        @NotNull(message = "Date of birth is required")
        LocalDate dateOfBirth,

        @NotNull(message = "Traveler name is required")
        @Valid
        TravelerName name,

        String gender,

        @Valid
        TravelerContact contact,

        List<TravelerDocument> documents
) {
    public TravelerDetails {
        documents = (documents == null) ? List.of() : List.copyOf(documents);
    }
}
