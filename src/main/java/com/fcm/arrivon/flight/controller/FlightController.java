package com.fcm.arrivon.flight.controller;

import com.fcm.arrivon.flight.dto.FlightBookingRequest;
import com.fcm.arrivon.flight.dto.FlightBookingResponse;
import com.fcm.arrivon.flight.dto.FlightBookingUpdateRequest;
import com.fcm.arrivon.flight.dto.FlightPricingRequest;
import com.fcm.arrivon.flight.dto.FlightPricingResponse;
import com.fcm.arrivon.flight.dto.FlightSearchRequest;
import com.fcm.arrivon.flight.dto.FlightSearchResponse;
import com.fcm.arrivon.flight.service.FlightBookingService;
import com.fcm.arrivon.flight.service.FlightOfferPricingService;
import com.fcm.arrivon.flight.service.FlightOfferSearchService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing flight search, pricing validation, booking creation, and order management.
 * Follows RESTful API conventions and delegates business logic to domain service interfaces.
 */
@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightOfferSearchService flightOfferSearchService;
    private final FlightOfferPricingService flightOfferPricingService;
    private final FlightBookingService flightBookingService;

    public FlightController(
            FlightOfferSearchService flightOfferSearchService,
            FlightOfferPricingService flightOfferPricingService,
            FlightBookingService flightBookingService
    ) {
        this.flightOfferSearchService = flightOfferSearchService;
        this.flightOfferPricingService = flightOfferPricingService;
        this.flightBookingService = flightBookingService;
    }

    /**
     * Searches for available flight offers matching the supplied search criteria.
     *
     * @param originLocationCode      3-letter IATA code for departure airport/city
     * @param destinationLocationCode 3-letter IATA code for arrival airport/city
     * @param departureDate           date of departure
     * @param returnDate              date of return (optional)
     * @param adultsCount             number of adult passengers (age 12+)
     * @param childrenCount           number of child passengers (age 2-11)
     * @param infantsCount            number of infant passengers (age under 2)
     * @param travelClass             travel cabin class (e.g. ECONOMY, BUSINESS)
     * @param currencyCode            preferred currency code (e.g. USD, EUR)
     * @param maximumPrice            maximum price ceiling
     * @param maximumResults          maximum count of offers to return
     * @return response entity containing search results
     */
    @GetMapping("/offers")
    public ResponseEntity<FlightSearchResponse> searchFlightOffers(
            @RequestParam String originLocationCode,
            @RequestParam String destinationLocationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(defaultValue = "1") Integer adultsCount,
            @RequestParam(required = false) Integer childrenCount,
            @RequestParam(required = false) Integer infantsCount,
            @RequestParam(required = false) String travelClass,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) Integer maximumPrice,
            @RequestParam(required = false) Integer maximumResults
    ) {
        FlightSearchRequest searchRequest = new FlightSearchRequest(
                originLocationCode,
                destinationLocationCode,
                departureDate,
                returnDate,
                adultsCount,
                childrenCount,
                infantsCount,
                travelClass,
                currencyCode,
                maximumPrice,
                maximumResults
        );

        FlightSearchResponse response = flightOfferSearchService.searchFlightOffers(searchRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirms and prices selected flight offers, returning detailed fare breakdown and tax items.
     *
     * @param pricingRequest request containing flight offers to price
     * @return response entity containing priced offer details
     */
    @PostMapping("/pricing")
    public ResponseEntity<FlightPricingResponse> priceFlightOffers(
            @Valid @RequestBody FlightPricingRequest pricingRequest
    ) {
        FlightPricingResponse response = flightOfferPricingService.priceFlightOffers(pricingRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a confirmed flight order / booking in the Global Distribution System.
     *
     * @param bookingRequest request containing flight offers, traveler information, and remarks
     * @return response entity containing created booking details and Location header
     */
    @PostMapping("/orders")
    public ResponseEntity<FlightBookingResponse> createFlightOrder(
            @Valid @RequestBody FlightBookingRequest bookingRequest
    ) {
        FlightBookingResponse response = flightBookingService.createFlightOrder(bookingRequest);
        URI locationUri = URI.create("/api/v1/flights/orders/" + response.flightOrderId());
        return ResponseEntity.created(locationUri).body(response);
    }

    /**
     * Retrieves an existing flight order by its unique order identifier.
     *
     * @param flightOrderId unique identifier of the flight order
     * @return response entity containing flight order details
     */
    @GetMapping("/orders/{flightOrderId}")
    public ResponseEntity<FlightBookingResponse> getFlightOrder(
            @PathVariable String flightOrderId
    ) {
        FlightBookingResponse response = flightBookingService.getFlightOrder(flightOrderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing flight order remarks or details.
     *
     * @param flightOrderId unique identifier of the flight order
     * @param updateRequest update payload containing modifications
     * @return response entity containing updated booking details
     */
    @PatchMapping("/orders/{flightOrderId}")
    public ResponseEntity<FlightBookingResponse> updateFlightOrder(
            @PathVariable String flightOrderId,
            @Valid @RequestBody FlightBookingUpdateRequest updateRequest
    ) {
        FlightBookingResponse response = flightBookingService.updateFlightOrder(flightOrderId, updateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels an existing flight booking order.
     *
     * @param flightOrderId unique identifier of the flight order to cancel
     * @return response entity with HTTP 204 No Content status
     */
    @DeleteMapping("/orders/{flightOrderId}")
    public ResponseEntity<Void> cancelFlightOrder(
            @PathVariable String flightOrderId
    ) {
        flightBookingService.cancelFlightOrder(flightOrderId);
        return ResponseEntity.noContent().build();
    }
}
