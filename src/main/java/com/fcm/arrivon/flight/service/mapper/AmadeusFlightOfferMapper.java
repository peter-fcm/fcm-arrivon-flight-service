package com.fcm.arrivon.flight.service.mapper;

import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.TravelerDetails;

/**
 * Interface defining mapping operations between Amadeus API client models and domain transfer records.
 */
public interface AmadeusFlightOfferMapper {

    FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.search.model.FlightOffer flightOffer);

    FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer flightOffer);

    FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOffer flightOffer);

    com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer toPricingFlightOffer(FlightOfferSummary flightOfferSummary);

    com.fcm.arrivon.flight.amadeus.client.order.create.model.Traveler toCreateTraveler(TravelerDetails travelerDetails);

    TravelerDetails toTravelerDetails(com.fcm.arrivon.flight.amadeus.client.order.management.model.Traveler traveler);
}
