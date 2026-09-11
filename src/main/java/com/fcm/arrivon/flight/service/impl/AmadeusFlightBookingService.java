package com.fcm.arrivon.flight.service.impl;

import com.fcm.arrivon.flight.amadeus.client.order.create.model.FlightOrder;
import com.fcm.arrivon.flight.amadeus.client.order.create.model.FlightOrderQuery;
import com.fcm.arrivon.flight.amadeus.client.order.create.model.GeneralRemark;
import com.fcm.arrivon.flight.amadeus.client.order.create.model.Remarks;
import com.fcm.arrivon.flight.amadeus.client.order.create.model.SuccessBooking;
import com.fcm.arrivon.flight.amadeus.client.order.create.model.Traveler;
import com.fcm.arrivon.flight.amadeus.client.order.management.model.UpdateQuery;
import com.fcm.arrivon.flight.dto.FlightBookingRequest;
import com.fcm.arrivon.flight.dto.FlightBookingResponse;
import com.fcm.arrivon.flight.dto.FlightBookingUpdateRequest;
import com.fcm.arrivon.flight.dto.FlightOfferSummary;
import com.fcm.arrivon.flight.dto.TravelerDetails;
import com.fcm.arrivon.flight.service.FlightBookingService;
import com.fcm.arrivon.flight.service.mapper.AmadeusFlightOfferMapper;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Service implementation for flight order booking, retrieval, modification, and cancellation via Amadeus APIs.
 */
@Service
public class AmadeusFlightBookingService implements FlightBookingService {

    private final com.fcm.arrivon.flight.amadeus.client.order.create.api.BookingApi createBookingApi;
    private final com.fcm.arrivon.flight.amadeus.client.order.management.api.BookingApi managementBookingApi;
    private final AmadeusFlightOfferMapper flightOfferMapper;

    public AmadeusFlightBookingService(
            com.fcm.arrivon.flight.amadeus.client.order.create.api.BookingApi flightCreateOrdersApi,
            com.fcm.arrivon.flight.amadeus.client.order.management.api.BookingApi flightOrderManagementApi,
            AmadeusFlightOfferMapper flightOfferMapper
    ) {
        this.createBookingApi = flightCreateOrdersApi;
        this.managementBookingApi = flightOrderManagementApi;
        this.flightOfferMapper = flightOfferMapper;
    }

    @Override
    public FlightBookingResponse createFlightOrder(FlightBookingRequest bookingRequest) {
        Objects.requireNonNull(bookingRequest, "bookingRequest must not be null");

        FlightOrder flightOrder = new FlightOrder();

        if (bookingRequest.travelers() != null) {
            List<Traveler> travelers = bookingRequest.travelers().stream()
                    .map(flightOfferMapper::toCreateTraveler)
                    .toList();
            flightOrder.setTravelers(travelers);
        }

        if (bookingRequest.remarks() != null && !bookingRequest.remarks().isBlank()) {
            Remarks remarks = new Remarks();
            GeneralRemark generalRemark = new GeneralRemark();
            generalRemark.setText(bookingRequest.remarks());
            remarks.setGeneral(List.of(generalRemark));
            flightOrder.setRemarks(remarks);
        }

        FlightOrderQuery flightOrderQuery = new FlightOrderQuery().data(flightOrder);
        SuccessBooking successResponse = createBookingApi.createFligtOrders(flightOrderQuery, null);

        String generatedOrderId = (successResponse != null)
                ? successResponse.getData().getId()
                : "UNKNOWN";

        return new FlightBookingResponse(
                generatedOrderId,
                "AMADEUS-ORDER-" + generatedOrderId,
                OffsetDateTime.now().toString(),
                "CONFIRMED",
                bookingRequest.flightOffers(),
                bookingRequest.travelers(),
                bookingRequest.remarks()
        );
    }

    @Override
    public FlightBookingResponse getFlightOrder(String flightOrderId) {
        Objects.requireNonNull(flightOrderId, "flightOrderId must not be null");

        com.fcm.arrivon.flight.amadeus.client.order.management.model.SuccessBooking response =
                managementBookingApi.getFlightOrder(flightOrderId, null, null);

        if (response == null) {
            return new FlightBookingResponse(
                    flightOrderId,
                    null,
                    null,
                    "UNKNOWN",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    null
            );
        }

        com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOrder order = response.getData();

        List<FlightOfferSummary> offers = order.getFlightOffers().stream()
                .map(flightOfferMapper::toOfferSummary)
                .toList();

        List<TravelerDetails> travelers = (order.getTravelers() == null)
                ? Collections.emptyList()
                : order.getTravelers().stream()
                        .map(flightOfferMapper::toTravelerDetails)
                        .toList();

        String remarksText = null;
        if (order.getRemarks() != null && order.getRemarks().getGeneral() != null && !order.getRemarks().getGeneral().isEmpty()) {
            remarksText = order.getRemarks().getGeneral().getFirst().getText();
        }

        return new FlightBookingResponse(
                order.getId(),
                "AMADEUS-REF-" + order.getId(),
                OffsetDateTime.now().toString(),
                "CONFIRMED",
                offers,
                travelers,
                remarksText
        );
    }

    @Override
    public FlightBookingResponse updateFlightOrder(String flightOrderId, FlightBookingUpdateRequest updateRequest) {
        Objects.requireNonNull(flightOrderId, "flightOrderId must not be null");
        Objects.requireNonNull(updateRequest, "updateRequest must not be null");

        UpdateQuery updateQuery = new UpdateQuery();
        com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOrder patchOrder =
                new com.fcm.arrivon.flight.amadeus.client.order.management.model.FlightOrder();

        if (updateRequest.remarks() != null) {
            com.fcm.arrivon.flight.amadeus.client.order.management.model.Remarks remarks =
                    new com.fcm.arrivon.flight.amadeus.client.order.management.model.Remarks();
            com.fcm.arrivon.flight.amadeus.client.order.management.model.GeneralRemark generalRemark =
                    new com.fcm.arrivon.flight.amadeus.client.order.management.model.GeneralRemark();
            generalRemark.setText(updateRequest.remarks());
            remarks.setGeneral(List.of(generalRemark));
            patchOrder.setRemarks(remarks);
        }

        updateQuery.setData(patchOrder);
        managementBookingApi.patchFlightOrder(flightOrderId, updateQuery);

        return new FlightBookingResponse(
                flightOrderId,
                "AMADEUS-REF-" + flightOrderId,
                OffsetDateTime.now().toString(),
                "UPDATED",
                Collections.emptyList(),
                Collections.emptyList(),
                updateRequest.remarks()
        );
    }

    @Override
    public void cancelFlightOrder(String flightOrderId) {
        Objects.requireNonNull(flightOrderId, "flightOrderId must not be null");
        managementBookingApi.cancelFlightOrder(flightOrderId);
    }
}
