package com.fcm.arrivon.flight.service.mapper;

import com.fcm.arrivon.flight.dto.FlightItinerarySummary;
import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.FlightPriceSummary;
import com.fcm.arrivon.flight.dto.FlightSegmentSummary;
import com.fcm.arrivon.flight.dto.TravelerContact;
import com.fcm.arrivon.flight.dto.TravelerDetails;
import com.fcm.arrivon.flight.dto.TravelerName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Component responsible for bidirectional mapping between Amadeus client models
 * and domain transfer records.
 */
@Component
public class DefaultAmadeusFlightOfferMapper implements AmadeusFlightOfferMapper {

    @Override
    public FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.search.model.FlightOffer flightOffer) {
        if (flightOffer == null) {
            return null;
        }

        List<FlightItinerarySummary> itinerarySummaries = flightOffer.getItineraries() == null
                ? Collections.emptyList()
                : flightOffer.getItineraries().stream()
                        .map(this::toSearchItinerarySummary)
                        .toList();

        FlightPriceSummary priceSummary = toSearchPriceSummary(flightOffer.getPrice());
        String offerSource = flightOffer.getSource() != null ? flightOffer.getSource().getValue() : null;

        return buildOfferSummary(
                flightOffer.getId(),
                offerSource,
                flightOffer.getNumberOfBookableSeats(),
                flightOffer.getOneWay(),
                itinerarySummaries,
                priceSummary,
                flightOffer.getValidatingAirlineCodes()
        );
    }

    @Override
    public FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer flightOffer) {
        if (flightOffer == null) {
            return null;
        }

        List<FlightItinerarySummary> itinerarySummaries = flightOffer.getItineraries() == null
                ? Collections.emptyList()
                : flightOffer.getItineraries().stream()
                        .map(this::toPriceItinerarySummary)
                        .toList();

        FlightPriceSummary priceSummary = toPricePriceSummary(flightOffer.getPrice());
        String offerSource = flightOffer.getSource() != null ? flightOffer.getSource().getValue() : null;

        return buildOfferSummary(
                flightOffer.getId(),
                offerSource,
                flightOffer.getNumberOfBookableSeats(),
                flightOffer.getOneWay(),
                itinerarySummaries,
                priceSummary,
                flightOffer.getValidatingAirlineCodes()
        );
    }

    @Override
    public FlightOfferSummary toOfferSummary(com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOffer flightOffer) {
        if (flightOffer == null) {
            return null;
        }

        List<FlightItinerarySummary> itinerarySummaries = flightOffer.getItineraries() == null
                ? Collections.emptyList()
                : flightOffer.getItineraries().stream()
                        .map(this::toManagementItinerarySummary)
                        .toList();

        FlightPriceSummary priceSummary = toManagementPriceSummary(flightOffer.getPrice());
        String offerSource = flightOffer.getSource() != null ? flightOffer.getSource().getValue() : null;

        return buildOfferSummary(
                flightOffer.getId(),
                offerSource,
                flightOffer.getNumberOfBookableSeats(),
                flightOffer.getOneWay(),
                itinerarySummaries,
                priceSummary,
                flightOffer.getValidatingAirlineCodes()
        );
    }

    @Override
    public com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer toPricingFlightOffer(FlightOfferSummary flightOfferSummary) {
        if (flightOfferSummary == null) {
            return null;
        }

        com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer offer =
                new com.fcm.arrivon.flight.amadeus.client.price.model.FlightOffer();
        offer.setId(flightOfferSummary.offerId());
        if (flightOfferSummary.source() != null) {
            try {
                offer.setSource(com.fcm.arrivon.flight.amadeus.client.price.model.FlightOfferSource.fromValue(flightOfferSummary.source()));
            } catch (IllegalArgumentException ignored) {
                // Ignore unexpected source values
            }
        }
        if (flightOfferSummary.numberOfBookableSeats() != null) {
            offer.setNumberOfBookableSeats(BigDecimal.valueOf(flightOfferSummary.numberOfBookableSeats()));
        }
        offer.setOneWay(flightOfferSummary.oneWay());

        if (flightOfferSummary.price() != null) {
            com.fcm.arrivon.flight.amadeus.client.price.model.ExtendedPrice price =
                    new com.fcm.arrivon.flight.amadeus.client.price.model.ExtendedPrice();
            price.setCurrency(flightOfferSummary.price().currency());
            price.setTotal(flightOfferSummary.price().total());
            price.setBase(flightOfferSummary.price().base());
            price.setGrandTotal(flightOfferSummary.price().grandTotal());
            offer.setPrice(price);
        }

        return offer;
    }

    @Override
    public com.fcm.arrivon.flight.amadeus.client.order.create.model.Traveler toCreateTraveler(TravelerDetails travelerDetails) {
        if (travelerDetails == null) {
            return null;
        }

        com.fcm.arrivon.flight.amadeus.client.order.create.model.Traveler traveler =
                new com.fcm.arrivon.flight.amadeus.client.order.create.model.Traveler();
        traveler.setId(travelerDetails.travelerId());
        traveler.setDateOfBirth(travelerDetails.dateOfBirth());

        if (travelerDetails.name() != null) {
            com.fcm.arrivon.flight.amadeus.client.order.create.model.Name name =
                    new com.fcm.arrivon.flight.amadeus.client.order.create.model.Name();
            name.setFirstName(travelerDetails.name().firstName());
            name.setLastName(travelerDetails.name().lastName());
            traveler.setName(name);
        }

        if (travelerDetails.contact() != null) {
            com.fcm.arrivon.flight.amadeus.client.order.create.model.Contact contact =
                    new com.fcm.arrivon.flight.amadeus.client.order.create.model.Contact();
            contact.setEmailAddress(travelerDetails.contact().emailAddress());
            traveler.setContact(contact);
        }

        return traveler;
    }

    @Override
    public TravelerDetails toTravelerDetails(com.fcm.arrivon.flight.amadeus.client.order.management.model.Traveler traveler) {
        if (traveler == null) {
            return null;
        }

        TravelerName name = (traveler.getName() != null)
                ? new TravelerName(traveler.getName().getFirstName(), traveler.getName().getLastName())
                : new TravelerName("", "");

        String email = (traveler.getContact() != null) ? traveler.getContact().getEmailAddress() : null;
        TravelerContact contact = new TravelerContact(email, null);

        return new TravelerDetails(
                traveler.getId(),
                traveler.getDateOfBirth(),
                name,
                traveler.getGender() != null ? traveler.getGender().getValue() : null,
                contact,
                Collections.emptyList()
        );
    }

    private FlightItinerarySummary toSearchItinerarySummary(com.fcm.arrivon.flight.amadeus.client.search.model.Itineraries itinerary) {
        List<FlightSegmentSummary> segmentSummaries = itinerary.getSegments().stream()
                .map(this::toSearchSegmentSummary)
                .toList();
        return new FlightItinerarySummary(itinerary.getDuration(), segmentSummaries);
    }

    private FlightSegmentSummary toSearchSegmentSummary(com.fcm.arrivon.flight.amadeus.client.search.model.Segment segment) {
        return toSegmentSummary(new SearchSegmentSource(segment));
    }

    private FlightPriceSummary toSearchPriceSummary(com.fcm.arrivon.flight.amadeus.client.search.model.ExtendedPrice price) {
        if (price == null) {
            return new FlightPriceSummary(null, null, null, null);
        }
        return new FlightPriceSummary(price.getCurrency(), price.getTotal(), price.getBase(), price.getGrandTotal());
    }

    private FlightItinerarySummary toPriceItinerarySummary(com.fcm.arrivon.flight.amadeus.client.price.model.Itineraries itinerary) {
        List<FlightSegmentSummary> segmentSummaries = itinerary.getSegments().stream()
                .map(this::toPriceSegmentSummary)
                .toList();
        return new FlightItinerarySummary(itinerary.getDuration(), segmentSummaries);
    }

    private FlightSegmentSummary toPriceSegmentSummary(com.fcm.arrivon.flight.amadeus.client.price.model.Segment segment) {
        return toSegmentSummary(new PriceSegmentSource(segment));
    }

    private FlightPriceSummary toPricePriceSummary(com.fcm.arrivon.flight.amadeus.client.price.model.ExtendedPrice price) {
        if (price == null) {
            return new FlightPriceSummary(null, null, null, null);
        }
        return new FlightPriceSummary(price.getCurrency(), price.getTotal(), price.getBase(), price.getGrandTotal());
    }

    private FlightItinerarySummary toManagementItinerarySummary(com.fcm.arrivon.flight.amadeus.client.order.management.model.Itineraries itinerary) {
        List<FlightSegmentSummary> segmentSummaries = itinerary.getSegments().stream()
                .map(this::toManagementSegmentSummary)
                .toList();
        return new FlightItinerarySummary(itinerary.getDuration(), segmentSummaries);
    }

    private FlightSegmentSummary toManagementSegmentSummary(com.fcm.arrivon.flight.amadeus.client.order.management.model.Segment segment) {
        return toSegmentSummary(new ManagementSegmentSource(segment));
    }

    private FlightPriceSummary toManagementPriceSummary(com.fcm.arrivon.flight.amadeus.client.order.management.model.ExtendedPrice price) {
        if (price == null) {
            return new FlightPriceSummary(null, null, null, null);
        }
        return new FlightPriceSummary(price.getCurrency(), price.getTotal(), price.getBase(), price.getGrandTotal());
    }

    private FlightOfferSummary buildOfferSummary(
            String offerId,
            String sourceValue,
            BigDecimal numberOfBookableSeats,
            Boolean oneWay,
            List<FlightItinerarySummary> itinerarySummaries,
            FlightPriceSummary priceSummary,
            List<String> validatingAirlineCodes
    ) {
        Integer bookableSeats = numberOfBookableSeats != null
                ? numberOfBookableSeats.intValue()
                : null;
        return new FlightOfferSummary(
                offerId,
                sourceValue,
                bookableSeats,
                oneWay,
                itinerarySummaries,
                priceSummary,
                validatingAirlineCodes
        );
    }

    private FlightSegmentSummary toSegmentSummary(SegmentDataSource source) {
        String departureTimeString = source.departureTime() != null ? source.departureTime().toString() : null;
        String arrivalTimeString = source.arrivalTime() != null ? source.arrivalTime().toString() : null;

        return new FlightSegmentSummary(
                source.departureAirportCode(),
                departureTimeString,
                source.arrivalAirportCode(),
                arrivalTimeString,
                source.carrierCode(),
                source.flightNumber(),
                source.aircraftCode(),
                source.duration(),
                source.numberOfStops()
        );
    }

    private interface SegmentDataSource {
        String departureAirportCode();
        OffsetDateTime departureTime();
        String arrivalAirportCode();
        OffsetDateTime arrivalTime();
        String carrierCode();
        String flightNumber();
        String aircraftCode();
        String duration();
        Integer numberOfStops();
    }

    private record SearchSegmentSource(com.fcm.arrivon.flight.amadeus.client.search.model.Segment segment) implements SegmentDataSource {
        @Override
        public String departureAirportCode() {
            return segment.getDeparture() != null ? segment.getDeparture().getIataCode() : null;
        }

        @Override
        public OffsetDateTime departureTime() {
            return segment.getDeparture() != null ? segment.getDeparture().getAt() : null;
        }

        @Override
        public String arrivalAirportCode() {
            return segment.getArrival() != null ? segment.getArrival().getIataCode() : null;
        }

        @Override
        public OffsetDateTime arrivalTime() {
            return segment.getArrival() != null ? segment.getArrival().getAt() : null;
        }

        @Override
        public String carrierCode() {
            return segment.getCarrierCode();
        }

        @Override
        public String flightNumber() {
            return segment.getNumber();
        }

        @Override
        public String aircraftCode() {
            return segment.getAircraft() != null ? segment.getAircraft().getCode() : null;
        }

        @Override
        public String duration() {
            return segment.getDuration();
        }

        @Override
        public Integer numberOfStops() {
            return segment.getNumberOfStops();
        }
    }

    private record PriceSegmentSource(com.fcm.arrivon.flight.amadeus.client.price.model.Segment segment) implements SegmentDataSource {
        @Override
        public String departureAirportCode() {
            return segment.getDeparture() != null ? segment.getDeparture().getIataCode() : null;
        }

        @Override
        public OffsetDateTime departureTime() {
            return segment.getDeparture() != null ? segment.getDeparture().getAt() : null;
        }

        @Override
        public String arrivalAirportCode() {
            return segment.getArrival() != null ? segment.getArrival().getIataCode() : null;
        }

        @Override
        public OffsetDateTime arrivalTime() {
            return segment.getArrival() != null ? segment.getArrival().getAt() : null;
        }

        @Override
        public String carrierCode() {
            return segment.getCarrierCode();
        }

        @Override
        public String flightNumber() {
            return segment.getNumber();
        }

        @Override
        public String aircraftCode() {
            return segment.getAircraft() != null ? segment.getAircraft().getCode() : null;
        }

        @Override
        public String duration() {
            return segment.getDuration();
        }

        @Override
        public Integer numberOfStops() {
            return segment.getNumberOfStops();
        }
    }

    private record ManagementSegmentSource(com.fcm.arrivon.flight.amadeus.client.order.management.model.Segment segment) implements SegmentDataSource {
        @Override
        public String departureAirportCode() {
            return segment.getDeparture() != null ? segment.getDeparture().getIataCode() : null;
        }

        @Override
        public OffsetDateTime departureTime() {
            return segment.getDeparture() != null ? segment.getDeparture().getAt() : null;
        }

        @Override
        public String arrivalAirportCode() {
            return segment.getArrival() != null ? segment.getArrival().getIataCode() : null;
        }

        @Override
        public OffsetDateTime arrivalTime() {
            return segment.getArrival() != null ? segment.getArrival().getAt() : null;
        }

        @Override
        public String carrierCode() {
            return segment.getCarrierCode();
        }

        @Override
        public String flightNumber() {
            return segment.getNumber();
        }

        @Override
        public String aircraftCode() {
            return segment.getAircraft() != null ? segment.getAircraft().getCode() : null;
        }

        @Override
        public String duration() {
            return segment.getDuration();
        }

        @Override
        public Integer numberOfStops() {
            return segment.getNumberOfStops();
        }
    }
}
