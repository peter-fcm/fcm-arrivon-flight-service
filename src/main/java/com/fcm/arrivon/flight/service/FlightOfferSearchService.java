package com.fcm.arrivon.flight.service;

import com.fcm.arrivon.flight.dto.FlightSearchRequest;
import com.fcm.arrivon.flight.dto.FlightSearchResponse;

/**
 * Service abstraction for searching flight offers via Amadeus FlightOffersSearch API.
 */
public interface FlightOfferSearchService {

    /**
     * Searches for available flight offers based on criteria such as origin, destination, and dates.
     *
     * @param searchRequest the search request parameters
     * @return search response containing matching flight offers
     */
    FlightSearchResponse searchFlightOffers(FlightSearchRequest searchRequest);
}
