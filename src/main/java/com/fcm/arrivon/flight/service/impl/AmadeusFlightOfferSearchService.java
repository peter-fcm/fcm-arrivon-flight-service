package com.fcm.arrivon.flight.service.impl;

import com.fcm.arrivon.flight.amadeus.client.search.api.ShoppingApi;
import com.fcm.arrivon.flight.amadeus.client.search.model.Success;
import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.FlightSearchRequest;
import com.fcm.arrivon.flight.dto.FlightSearchResponse;
import com.fcm.arrivon.flight.service.FlightOfferSearchService;
import com.fcm.arrivon.flight.service.mapper.AmadeusFlightOfferMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Service implementation for searching flight offers using Amadeus FlightOffersSearch REST API.
 */
@Service
public class AmadeusFlightOfferSearchService implements FlightOfferSearchService {

    private final ShoppingApi shoppingApi;
    private final AmadeusFlightOfferMapper flightOfferMapper;

    public AmadeusFlightOfferSearchService(ShoppingApi shoppingApi, AmadeusFlightOfferMapper flightOfferMapper) {
        this.shoppingApi = shoppingApi;
        this.flightOfferMapper = flightOfferMapper;
    }

    @Override
    public FlightSearchResponse searchFlightOffers(FlightSearchRequest searchRequest) {
        Objects.requireNonNull(searchRequest, "searchRequest must not be null");

        Success response = shoppingApi.getFlightOffers(
                searchRequest.originLocationCode(),
                searchRequest.destinationLocationCode(),
                searchRequest.departureDate(),
                searchRequest.adultsCount(),
                searchRequest.returnDate(),
                searchRequest.childrenCount(),
                searchRequest.infantsCount(),
                searchRequest.travelClass(),
                null,
                null,
                null,
                searchRequest.currencyCode(),
                searchRequest.maximumPrice(),
                searchRequest.maximumResults()
        );

        if (response == null) {
            return new FlightSearchResponse(0, Collections.emptyList());
        }

        List<FlightOfferSummary> offerSummaries = response.getData().stream()
                .map(flightOfferMapper::toOfferSummary)
                .toList();

        return new FlightSearchResponse(offerSummaries.size(), offerSummaries);
    }
}
