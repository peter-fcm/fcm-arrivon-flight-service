package com.fcm.arrivon.flight.service.mapper;

import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.FlightPriceSummary;
import com.fcm.arrivon.flight.dto.TravelerContact;
import com.fcm.arrivon.flight.dto.TravelerDetails;
import com.fcm.arrivon.flight.dto.TravelerName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAmadeusFlightOfferMapperTest {

    private final DefaultAmadeusFlightOfferMapper mapper = new DefaultAmadeusFlightOfferMapper();

    @Test
    @DisplayName("Should map Amadeus Search FlightOffer to FlightOfferSummary")
    void shouldMapSearchFlightOfferToSummary() {
        var flightOffer = new com.fcm.arrivon.flight.amadeus.client.search.model.FlightOffer();
        flightOffer.setId("1");
        flightOffer.setSource(com.fcm.arrivon.flight.amadeus.client.search.model.FlightOfferSource.GDS);
        flightOffer.setNumberOfBookableSeats(BigDecimal.valueOf(9));
        flightOffer.setOneWay(false);
        flightOffer.setValidatingAirlineCodes(List.of("QF"));

        var price = new com.fcm.arrivon.flight.amadeus.client.search.model.ExtendedPrice();
        price.setCurrency("USD");
        price.setTotal("450.00");
        price.setBase("380.00");
        price.setGrandTotal("450.00");
        flightOffer.setPrice(price);

        var itinerary = new com.fcm.arrivon.flight.amadeus.client.search.model.Itineraries();
        itinerary.setDuration("PT6H00M");

        var segment = new com.fcm.arrivon.flight.amadeus.client.search.model.Segment();
        var departure = new com.fcm.arrivon.flight.amadeus.client.search.model.FlightEndPoint();
        departure.setIataCode("SYD");
        departure.setAt(OffsetDateTime.parse("2026-11-01T06:00:00Z"));
        segment.setDeparture(departure);

        var arrival = new com.fcm.arrivon.flight.amadeus.client.search.model.FlightEndPoint();
        arrival.setIataCode("BKK");
        arrival.setAt(OffsetDateTime.parse("2026-11-01T12:00:00Z"));
        segment.setArrival(arrival);

        segment.setCarrierCode("QF");
        segment.setNumber("291");
        segment.setDuration("PT6H00M");
        segment.setNumberOfStops(0);

        itinerary.setSegments(List.of(segment));
        flightOffer.setItineraries(List.of(itinerary));

        FlightOfferSummary summary = mapper.toOfferSummary(flightOffer);

        assertThat(summary).isNotNull();
        assertThat(summary.offerId()).isEqualTo("1");
        assertThat(summary.source()).isEqualTo("GDS");
        assertThat(summary.numberOfBookableSeats()).isEqualTo(9);
        assertThat(summary.price()).isNotNull();
        assertThat(summary.price().grandTotal()).isEqualTo("450.00");
        assertThat(summary.itineraries()).hasSize(1);
        assertThat(summary.itineraries().getFirst().segments().getFirst().departureAirportCode()).isEqualTo("SYD");
        assertThat(summary.itineraries().getFirst().segments().getFirst().arrivalAirportCode()).isEqualTo("BKK");
    }

    @Test
    @DisplayName("Should map FlightOfferSummary to Pricing FlightOffer")
    void shouldMapSummaryToPricingFlightOffer() {
        FlightPriceSummary priceSummary = new FlightPriceSummary("USD", "450.00", "380.00", "450.00");
        FlightOfferSummary summary = new FlightOfferSummary("1", "GDS", 9, false, List.of(), priceSummary, List.of("QF"));

        var pricingOffer = mapper.toPricingFlightOffer(summary);

        assertThat(pricingOffer).isNotNull();
        assertThat(pricingOffer.getId()).isEqualTo("1");
        assertThat(pricingOffer.getSource()).isNotNull();
        assertThat(pricingOffer.getSource().getValue()).isEqualTo("GDS");
        assertThat(pricingOffer.getNumberOfBookableSeats()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(pricingOffer.getPrice()).isNotNull();
        assertThat(pricingOffer.getPrice().getTotal()).isEqualTo("450.00");
    }

    @Test
    @DisplayName("Should map TravelerDetails to Create Traveler")
    void shouldMapTravelerDetailsToCreateTraveler() {
        TravelerName name = new TravelerName("Jane", "Doe");
        TravelerContact contact = new TravelerContact("jane.doe@example.com", "+1-555-0123");
        TravelerDetails details = new TravelerDetails("1", LocalDate.of(1990, 1, 1), name, "FEMALE", contact, List.of());

        var traveler = mapper.toCreateTraveler(details);

        assertThat(traveler).isNotNull();
        assertThat(traveler.getId()).isEqualTo("1");
        assertThat(traveler.getName()).isNotNull();
        assertThat(traveler.getName().getFirstName()).isEqualTo("Jane");
        assertThat(traveler.getName().getLastName()).isEqualTo("Doe");
        assertThat(traveler.getContact()).isNotNull();
        assertThat(traveler.getContact().getEmailAddress()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("Should return null safely on null inputs")
    void shouldHandleNullInputsSafely() {
        assertThat(mapper.toOfferSummary((com.fcm.arrivon.flight.amadeus.client.search.model.FlightOffer) null)).isNull();
        assertThat(mapper.toOfferSummary((com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer) null)).isNull();
        assertThat(mapper.toOfferSummary((com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOffer) null)).isNull();
        assertThat(mapper.toPricingFlightOffer(null)).isNull();
        assertThat(mapper.toCreateTraveler(null)).isNull();
        assertThat(mapper.toTravelerDetails(null)).isNull();
    }
}
