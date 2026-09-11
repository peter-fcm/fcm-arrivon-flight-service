package com.fcm.arrivon.flight.service;

import com.fcm.arrivon.flight.dto.FlightPricingRequest;
import com.fcm.arrivon.flight.dto.FlightPricingResponse;

/**
 * Service abstraction for confirming flight offer pricing and availability via Amadeus FlightOffersPrice API.
 */
public interface FlightOfferPricingService {

    /**
     * Validates and prices the specified flight offer(s).
     *
     * @param pricingRequest the pricing request containing selected flight offers
     * @return pricing response containing verified pricing details and fare rules
     */
    FlightPricingResponse priceFlightOffers(FlightPricingRequest pricingRequest);
}
