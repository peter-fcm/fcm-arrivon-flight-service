package com.fcm.arrivon.flight.service.impl;

import com.fcm.arrivon.flight.amadeus.client.price.api.ShoppingApi;
import com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer;
import com.fcm.arrivon.flight.amadeus.client.price.model.FlightOfferPricingIn;
import com.fcm.arrivon.flight.amadeus.client.price.model.GetPriceQuery;
import com.fcm.arrivon.flight.amadeus.client.price.model.SuccessPricing;
import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.FlightPricingRequest;
import com.fcm.arrivon.flight.dto.FlightPricingResponse;
import com.fcm.arrivon.flight.service.FlightOfferPricingService;
import com.fcm.arrivon.flight.service.mapper.AmadeusFlightOfferMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Service implementation for confirming flight offer pricing using Amadeus FlightOffersPrice REST API.
 */
@Service
public class AmadeusFlightOfferPricingService implements FlightOfferPricingService {

    private final ShoppingApi shoppingApi;
    private final AmadeusFlightOfferMapper flightOfferMapper;

    public AmadeusFlightOfferPricingService(ShoppingApi shoppingApi, AmadeusFlightOfferMapper flightOfferMapper) {
        this.shoppingApi = shoppingApi;
        this.flightOfferMapper = flightOfferMapper;
    }

    @Override
    public FlightPricingResponse priceFlightOffers(FlightPricingRequest pricingRequest) {
        Objects.requireNonNull(pricingRequest, "pricingRequest must not be null");

        List<FlightOffer> clientOffers = pricingRequest.flightOffers().stream()
                .map(flightOfferMapper::toPricingFlightOffer)
                .toList();

        GetPriceQuery getPriceQuery = new GetPriceQuery()
                .data(new FlightOfferPricingIn().flightOffers(clientOffers));

        SuccessPricing response = shoppingApi.quoteAirOffers(
                getPriceQuery,
                pricingRequest.includeOtherServices(),
                null
        );

        if (response == null) {
            return new FlightPricingResponse("flight-offers-pricing", Collections.emptyList());
        }

        List<FlightOfferSummary> pricedOffers = response.getData().getFlightOffers().stream()
                .map(flightOfferMapper::toOfferSummary)
                .toList();

        return new FlightPricingResponse(response.getData().getType(), pricedOffers);
    }
}
