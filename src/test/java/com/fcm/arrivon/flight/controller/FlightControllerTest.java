package com.fcm.arrivon.flight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fcm.arrivon.flight.dto.FlightBookingRequest;
import com.fcm.arrivon.flight.dto.FlightBookingResponse;
import com.fcm.arrivon.flight.dto.FlightBookingUpdateRequest;
import com.fcm.arrivon.flight.dto.FlightItinerarySummary;
import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.FlightPriceSummary;
import com.fcm.arrivon.flight.dto.FlightPricingRequest;
import com.fcm.arrivon.flight.dto.FlightPricingResponse;
import com.fcm.arrivon.flight.dto.FlightSearchRequest;
import com.fcm.arrivon.flight.dto.FlightSearchResponse;
import com.fcm.arrivon.flight.dto.FlightSegmentSummary;
import com.fcm.arrivon.flight.dto.TravelerContact;
import com.fcm.arrivon.flight.dto.TravelerDetails;
import com.fcm.arrivon.flight.dto.TravelerDocument;
import com.fcm.arrivon.flight.dto.TravelerName;
import com.fcm.arrivon.flight.service.FlightBookingService;
import com.fcm.arrivon.flight.service.FlightOfferPricingService;
import com.fcm.arrivon.flight.service.FlightOfferSearchService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private FlightOfferSearchService flightOfferSearchService;

    @MockitoBean
    private FlightOfferPricingService flightOfferPricingService;

    @MockitoBean
    private FlightBookingService flightBookingService;

    private FlightOfferSummary createSampleFlightOfferSummary() {
        FlightSegmentSummary outboundSegment = new FlightSegmentSummary(
                "SYD",
                "2026-11-01T06:00:00",
                "BKK",
                "2026-11-01T12:00:00",
                "QF",
                "291",
                "330",
                "PT6H00M",
                0
        );

        FlightItinerarySummary itinerarySummary = new FlightItinerarySummary(
                "PT6H00M",
                List.of(outboundSegment)
        );

        FlightPriceSummary priceSummary = new FlightPriceSummary(
                "USD",
                "450.00",
                "380.00",
                "450.00"
        );

        return new FlightOfferSummary(
                "1",
                "GDS",
                9,
                false,
                List.of(itinerarySummary),
                priceSummary,
                List.of("QF")
        );
    }

    private TravelerDetails createSampleTravelerDetails() {
        TravelerName travelerName = new TravelerName("John", "Doe");
        TravelerContact travelerContact = new TravelerContact("john.doe@example.com", "+1-555-0199");
        TravelerDocument travelerDocument = new TravelerDocument(
                "PASSPORT",
                "A12345678",
                "USA",
                LocalDate.of(2030, 1, 1),
                "USA",
                true
        );

        return new TravelerDetails(
                "1",
                LocalDate.of(1988, 5, 20),
                travelerName,
                "MALE",
                travelerContact,
                List.of(travelerDocument)
        );
    }

    @Test
    @DisplayName("GET /api/v1/flights/offers - Search Flight Offers")
    void searchFlightOffers_shouldReturnOffersAndGenerateDocumentation() throws Exception {
        FlightOfferSummary offerSummary = createSampleFlightOfferSummary();
        FlightSearchResponse searchResponse = new FlightSearchResponse(1, List.of(offerSummary));

        Mockito.when(flightOfferSearchService.searchFlightOffers(any(FlightSearchRequest.class)))
                .thenReturn(searchResponse);

        mockMvc.perform(get("/api/v1/flights/offers")
                        .param("originLocationCode", "SYD")
                        .param("destinationLocationCode", "BKK")
                        .param("departureDate", "2026-11-01")
                        .param("returnDate", "2026-11-15")
                        .param("adultsCount", "1")
                        .param("travelClass", "ECONOMY")
                        .param("currencyCode", "USD")
                        .param("maximumResults", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOffers").value(1))
                .andExpect(jsonPath("$.flightOffers[0].offerId").value("1"))
                .andDo(document("flight-offers-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("originLocationCode").description("3-letter IATA code of origin airport"),
                                parameterWithName("destinationLocationCode").description("3-letter IATA code of destination airport"),
                                parameterWithName("departureDate").description("Departure date (ISO 8601: YYYY-MM-DD)"),
                                parameterWithName("returnDate").optional().description("Return date for round-trip (ISO 8601: YYYY-MM-DD)"),
                                parameterWithName("adultsCount").description("Number of adult passengers"),
                                parameterWithName("childrenCount").optional().description("Number of child passengers"),
                                parameterWithName("infantsCount").optional().description("Number of infant passengers"),
                                parameterWithName("travelClass").optional().description("Cabin travel class (ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST)"),
                                parameterWithName("currencyCode").optional().description("Preferred currency code (e.g. USD, EUR)"),
                                parameterWithName("maximumPrice").optional().description("Maximum budget ceiling"),
                                parameterWithName("maximumResults").optional().description("Maximum number of offers to return")
                        ),
                        responseFields(
                                fieldWithPath("totalOffers").description("Total number of available offers found"),
                                fieldWithPath("flightOffers").description("List of matching flight offers"),
                                fieldWithPath("flightOffers[].offerId").description("Unique identifier of the flight offer"),
                                fieldWithPath("flightOffers[].source").description("Global distribution system source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Number of bookable seats remaining"),
                                fieldWithPath("flightOffers[].oneWay").description("One way indicator"),
                                fieldWithPath("flightOffers[].itineraries").description("List of trip itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Total duration of the itinerary in ISO 8601 format"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Flight segments belonging to this itinerary"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure airport 3-letter IATA code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure timestamp"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival airport 3-letter IATA code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival timestamp"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Operating airline 2-letter code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("IATA aircraft equipment code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Segment flight duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Number of intermediate stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency code for pricing"),
                                fieldWithPath("flightOffers[].price.total").description("Total price including all taxes and fees"),
                                fieldWithPath("flightOffers[].price.base").description("Base ticket fare before taxes"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total amount payable"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Airlines validating this offer")
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/flights/pricing - Confirm Offer Pricing")
    void priceFlightOffers_shouldReturnPricingAndGenerateDocumentation() throws Exception {
        FlightOfferSummary offerSummary = createSampleFlightOfferSummary();
        FlightPricingRequest pricingRequest = new FlightPricingRequest(List.of(offerSummary), List.of("other-services"));
        FlightPricingResponse pricingResponse = new FlightPricingResponse("flight-offers-pricing", List.of(offerSummary));

        Mockito.when(flightOfferPricingService.priceFlightOffers(any(FlightPricingRequest.class)))
                .thenReturn(pricingResponse);

        mockMvc.perform(post("/api/v1/flights/pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pricingRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pricingType").value("flight-offers-pricing"))
                .andExpect(jsonPath("$.flightOffers[0].offerId").value("1"))
                .andDo(document("flight-offers-price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("flightOffers").description("List of flight offers to price and confirm"),
                                fieldWithPath("flightOffers[].offerId").description("Identifier of the offer to price"),
                                fieldWithPath("flightOffers[].source").description("Global distribution system source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Remaining bookable seats"),
                                fieldWithPath("flightOffers[].oneWay").description("Whether this is a one-way trip"),
                                fieldWithPath("flightOffers[].itineraries").description("List of trip itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Itinerary duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Flight segments"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure airport code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival airport code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Carrier code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("Aircraft code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Intermediate stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency"),
                                fieldWithPath("flightOffers[].price.total").description("Total price"),
                                fieldWithPath("flightOffers[].price.base").description("Base fare"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Validating airline codes"),
                                fieldWithPath("includeOtherServices").optional().description("Ancillary services to include in quotation")
                        ),
                        responseFields(
                                fieldWithPath("pricingType").description("Type of pricing resource"),
                                fieldWithPath("flightOffers").description("List of confirmed flight offers"),
                                fieldWithPath("flightOffers[].offerId").description("Identifier of confirmed offer"),
                                fieldWithPath("flightOffers[].source").description("GDS source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Bookable seats"),
                                fieldWithPath("flightOffers[].oneWay").description("One way indicator"),
                                fieldWithPath("flightOffers[].itineraries").description("Itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Segments"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Carrier"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("Aircraft code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Segment duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency"),
                                fieldWithPath("flightOffers[].price.total").description("Total"),
                                fieldWithPath("flightOffers[].price.base").description("Base fare"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Validating airlines")
                        )
                ));
    }

    @Test
    @DisplayName("POST /api/v1/flights/orders - Create Flight Order Booking")
    void createFlightOrder_shouldCreateOrderAndGenerateDocumentation() throws Exception {
        FlightOfferSummary offerSummary = createSampleFlightOfferSummary();
        TravelerDetails travelerDetails = createSampleTravelerDetails();
        FlightBookingRequest bookingRequest = new FlightBookingRequest(
                List.of(offerSummary),
                List.of(travelerDetails),
                "Customer requested aisle seating."
        );

        FlightBookingResponse bookingResponse = new FlightBookingResponse(
                "eJzTd9f3NjQ28zEAAAn%2BAf8%3D",
                "AMADEUS-ORDER-eJzTd9f3NjQ28zEAAAn%2BAf8%3D",
                "2026-11-01T10:00:00Z",
                "CONFIRMED",
                List.of(offerSummary),
                List.of(travelerDetails),
                "Customer requested aisle seating."
        );

        Mockito.when(flightBookingService.createFlightOrder(any(FlightBookingRequest.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/api/v1/flights/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/flights/orders/eJzTd9f3NjQ28zEAAAn%2BAf8%3D"))
                .andExpect(jsonPath("$.flightOrderId").value("eJzTd9f3NjQ28zEAAAn%2BAf8%3D"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"))
                .andDo(document("flight-orders-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseHeaders(
                                headerWithName("Location").description("URI of the newly created flight booking order resource")
                        ),
                        requestFields(
                                fieldWithPath("flightOffers").description("Flight offers selected for booking"),
                                fieldWithPath("flightOffers[].offerId").description("Offer identifier"),
                                fieldWithPath("flightOffers[].source").description("GDS source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Bookable seats"),
                                fieldWithPath("flightOffers[].oneWay").description("One way indicator"),
                                fieldWithPath("flightOffers[].itineraries").description("Itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Segments"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival code"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Carrier"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("Aircraft"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency"),
                                fieldWithPath("flightOffers[].price.total").description("Total"),
                                fieldWithPath("flightOffers[].price.base").description("Base fare"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Validating airlines"),
                                fieldWithPath("travelers").description("List of travelers / passengers"),
                                fieldWithPath("travelers[].travelerId").description("Unique traveler ID within booking"),
                                fieldWithPath("travelers[].dateOfBirth").description("Date of birth (YYYY-MM-DD)"),
                                fieldWithPath("travelers[].name.firstName").description("Given first name"),
                                fieldWithPath("travelers[].name.lastName").description("Family last name"),
                                fieldWithPath("travelers[].gender").description("Gender (MALE / FEMALE)"),
                                fieldWithPath("travelers[].contact.emailAddress").description("Contact email address"),
                                fieldWithPath("travelers[].contact.phoneNumber").description("Contact phone number"),
                                fieldWithPath("travelers[].documents").description("Identification documents"),
                                fieldWithPath("travelers[].documents[].documentType").description("Document type (e.g. PASSPORT)"),
                                fieldWithPath("travelers[].documents[].documentNumber").description("Document serial number"),
                                fieldWithPath("travelers[].documents[].issuanceCountry").description("Issuing country code"),
                                fieldWithPath("travelers[].documents[].expiryDate").description("Document expiration date"),
                                fieldWithPath("travelers[].documents[].nationality").description("Traveler nationality"),
                                fieldWithPath("travelers[].documents[].holder").description("Holder indicator"),
                                fieldWithPath("remarks").optional().description("Special service remarks or notes")
                        ),
                        responseFields(
                                fieldWithPath("flightOrderId").description("GDS unique booking order identifier"),
                                fieldWithPath("reference").description("Booking reference code"),
                                fieldWithPath("creationDateTime").description("Timestamp when booking was created"),
                                fieldWithPath("bookingStatus").description("Current booking status (e.g. CONFIRMED)"),
                                fieldWithPath("flightOffers").description("Booked flight offers"),
                                fieldWithPath("flightOffers[].offerId").description("Offer ID"),
                                fieldWithPath("flightOffers[].source").description("Source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Seats"),
                                fieldWithPath("flightOffers[].oneWay").description("One way"),
                                fieldWithPath("flightOffers[].itineraries").description("Itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Segments"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure airport"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival airport"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Carrier"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("Aircraft"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency"),
                                fieldWithPath("flightOffers[].price.total").description("Total"),
                                fieldWithPath("flightOffers[].price.base").description("Base fare"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Validating airlines"),
                                fieldWithPath("travelers").description("Confirmed travelers"),
                                fieldWithPath("travelers[].travelerId").description("Traveler ID"),
                                fieldWithPath("travelers[].dateOfBirth").description("Date of birth"),
                                fieldWithPath("travelers[].name.firstName").description("First name"),
                                fieldWithPath("travelers[].name.lastName").description("Last name"),
                                fieldWithPath("travelers[].gender").description("Gender"),
                                fieldWithPath("travelers[].contact.emailAddress").description("Email"),
                                fieldWithPath("travelers[].contact.phoneNumber").description("Phone"),
                                fieldWithPath("travelers[].documents").description("Documents"),
                                fieldWithPath("travelers[].documents[].documentType").description("Document type"),
                                fieldWithPath("travelers[].documents[].documentNumber").description("Number"),
                                fieldWithPath("travelers[].documents[].issuanceCountry").description("Issuing country"),
                                fieldWithPath("travelers[].documents[].expiryDate").description("Expiry date"),
                                fieldWithPath("travelers[].documents[].nationality").description("Nationality"),
                                fieldWithPath("travelers[].documents[].holder").description("Holder"),
                                fieldWithPath("remarks").description("Booking remarks")
                        )
                ));
    }

    @Test
    @DisplayName("GET /api/v1/flights/orders/{flightOrderId} - Retrieve Flight Order")
    void getFlightOrder_shouldReturnOrderAndGenerateDocumentation() throws Exception {
        String flightOrderId = "eJzTd9f3NjQ28zEAAAn%2BAf8%3D";
        FlightOfferSummary offerSummary = createSampleFlightOfferSummary();
        TravelerDetails travelerDetails = createSampleTravelerDetails();

        FlightBookingResponse bookingResponse = new FlightBookingResponse(
                flightOrderId,
                "AMADEUS-ORDER-" + flightOrderId,
                "2026-11-01T10:00:00Z",
                "CONFIRMED",
                List.of(offerSummary),
                List.of(travelerDetails),
                "Customer requested aisle seating."
        );

        Mockito.when(flightBookingService.getFlightOrder(eq(flightOrderId)))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/api/v1/flights/orders/{flightOrderId}", flightOrderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightOrderId").value(flightOrderId))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"))
                .andDo(document("flight-orders-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("flightOrderId").description("Unique identifier of the flight booking order")
                        ),
                        responseFields(
                                fieldWithPath("flightOrderId").description("GDS unique booking order identifier"),
                                fieldWithPath("reference").description("Booking reference code"),
                                fieldWithPath("creationDateTime").description("Timestamp when booking was created"),
                                fieldWithPath("bookingStatus").description("Current booking status"),
                                fieldWithPath("flightOffers").description("Booked flight offers"),
                                fieldWithPath("flightOffers[].offerId").description("Offer ID"),
                                fieldWithPath("flightOffers[].source").description("Source"),
                                fieldWithPath("flightOffers[].numberOfBookableSeats").description("Seats"),
                                fieldWithPath("flightOffers[].oneWay").description("One way"),
                                fieldWithPath("flightOffers[].itineraries").description("Itineraries"),
                                fieldWithPath("flightOffers[].itineraries[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments").description("Segments"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureAirportCode").description("Departure airport"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].departureTime").description("Departure time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalAirportCode").description("Arrival airport"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].arrivalTime").description("Arrival time"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].carrierCode").description("Carrier"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].flightNumber").description("Flight number"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].aircraftCode").description("Aircraft"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].duration").description("Duration"),
                                fieldWithPath("flightOffers[].itineraries[].segments[].numberOfStops").description("Stops"),
                                fieldWithPath("flightOffers[].price.currency").description("Currency"),
                                fieldWithPath("flightOffers[].price.total").description("Total"),
                                fieldWithPath("flightOffers[].price.base").description("Base fare"),
                                fieldWithPath("flightOffers[].price.grandTotal").description("Grand total"),
                                fieldWithPath("flightOffers[].validatingAirlineCodes").description("Validating airlines"),
                                fieldWithPath("travelers").description("Confirmed travelers"),
                                fieldWithPath("travelers[].travelerId").description("Traveler ID"),
                                fieldWithPath("travelers[].dateOfBirth").description("Date of birth"),
                                fieldWithPath("travelers[].name.firstName").description("First name"),
                                fieldWithPath("travelers[].name.lastName").description("Last name"),
                                fieldWithPath("travelers[].gender").description("Gender"),
                                fieldWithPath("travelers[].contact.emailAddress").description("Email"),
                                fieldWithPath("travelers[].contact.phoneNumber").description("Phone"),
                                fieldWithPath("travelers[].documents").description("Documents"),
                                fieldWithPath("travelers[].documents[].documentType").description("Document type"),
                                fieldWithPath("travelers[].documents[].documentNumber").description("Number"),
                                fieldWithPath("travelers[].documents[].issuanceCountry").description("Issuing country"),
                                fieldWithPath("travelers[].documents[].expiryDate").description("Expiry date"),
                                fieldWithPath("travelers[].documents[].nationality").description("Nationality"),
                                fieldWithPath("travelers[].documents[].holder").description("Holder"),
                                fieldWithPath("remarks").description("Booking remarks")
                        )
                ));
    }

    @Test
    @DisplayName("PATCH /api/v1/flights/orders/{flightOrderId} - Modify Flight Order")
    void updateFlightOrder_shouldUpdateOrderAndGenerateDocumentation() throws Exception {
        String flightOrderId = "eJzTd9f3NjQ28zEAAAn%2BAf8%3D";
        FlightBookingUpdateRequest updateRequest = new FlightBookingUpdateRequest("Updated remark: passenger requires wheelchair assistance.");

        FlightBookingResponse updateResponse = new FlightBookingResponse(
                flightOrderId,
                "AMADEUS-ORDER-" + flightOrderId,
                "2026-11-01T10:00:00Z",
                "UPDATED",
                List.of(),
                List.of(),
                "Updated remark: passenger requires wheelchair assistance."
        );

        Mockito.when(flightBookingService.updateFlightOrder(eq(flightOrderId), any(FlightBookingUpdateRequest.class)))
                .thenReturn(updateResponse);

        mockMvc.perform(patch("/api/v1/flights/orders/{flightOrderId}", flightOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightOrderId").value(flightOrderId))
                .andExpect(jsonPath("$.bookingStatus").value("UPDATED"))
                .andDo(document("flight-orders-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("flightOrderId").description("Unique identifier of the flight booking order")
                        ),
                        requestFields(
                                fieldWithPath("remarks").description("Updated remarks or special instructions")
                        ),
                        responseFields(
                                fieldWithPath("flightOrderId").description("GDS unique booking order identifier"),
                                fieldWithPath("reference").description("Booking reference code"),
                                fieldWithPath("creationDateTime").description("Timestamp when booking was created"),
                                fieldWithPath("bookingStatus").description("Updated booking status"),
                                fieldWithPath("flightOffers").description("Flight offers associated with order"),
                                fieldWithPath("travelers").description("Travelers associated with order"),
                                fieldWithPath("remarks").description("Updated booking remarks")
                        )
                ));
    }

    @Test
    @DisplayName("DELETE /api/v1/flights/orders/{flightOrderId} - Cancel Flight Order")
    void cancelFlightOrder_shouldCancelOrderAndGenerateDocumentation() throws Exception {
        String flightOrderId = "eJzTd9f3NjQ28zEAAAn%2BAf8%3D";

        Mockito.doNothing().when(flightBookingService).cancelFlightOrder(eq(flightOrderId));

        mockMvc.perform(delete("/api/v1/flights/orders/{flightOrderId}", flightOrderId))
                .andExpect(status().isNoContent())
                .andDo(document("flight-orders-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("flightOrderId").description("Unique identifier of the flight booking order to cancel")
                        )
                ));

        Mockito.verify(flightBookingService).cancelFlightOrder(eq(flightOrderId));
    }
}
