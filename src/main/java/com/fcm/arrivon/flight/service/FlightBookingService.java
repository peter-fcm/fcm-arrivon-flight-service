package com.fcm.arrivon.flight.service;

import com.fcm.arrivon.flight.dto.FlightBookingRequest;
import com.fcm.arrivon.flight.dto.FlightBookingResponse;
import com.fcm.arrivon.flight.dto.FlightBookingUpdateRequest;

/**
 * Service abstraction for managing the full lifecycle of flight booking orders via Amadeus APIs.
 */
public interface FlightBookingService {

    /**
     * Creates a confirmed flight order / booking.
     *
     * @param bookingRequest the booking request containing flight offers and traveler details
     * @return confirmed flight booking response
     */
    FlightBookingResponse createFlightOrder(FlightBookingRequest bookingRequest);

    /**
     * Retrieves an existing flight order by its unique identifier.
     *
     * @param flightOrderId unique flight order identifier
     * @return retrieved flight booking details
     */
    FlightBookingResponse getFlightOrder(String flightOrderId);

    /**
     * Updates an existing flight order, such as modifying remarks or passenger information.
     *
     * @param flightOrderId unique flight order identifier
     * @param updateRequest the update payload
     * @return updated flight booking response
     */
    FlightBookingResponse updateFlightOrder(String flightOrderId, FlightBookingUpdateRequest updateRequest);

    /**
     * Cancels an existing flight order in the global distribution system.
     *
     * @param flightOrderId unique flight order identifier
     */
    void cancelFlightOrder(String flightOrderId);
}
