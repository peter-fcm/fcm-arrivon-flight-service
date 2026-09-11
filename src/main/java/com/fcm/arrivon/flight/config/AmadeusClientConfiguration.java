package com.fcm.arrivon.flight.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration defining beans for Amadeus REST API clients.
 */
@Configuration
@EnableConfigurationProperties(AmadeusProperties.class)
public class AmadeusClientConfiguration {

    private final AmadeusProperties amadeusProperties;

    public AmadeusClientConfiguration(AmadeusProperties amadeusProperties) {
        this.amadeusProperties = amadeusProperties;
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.search.invoker.ApiClient flightSearchApiClient() {
        com.fcm.arrivon.flight.amadeus.client.search.invoker.ApiClient apiClient =
                new com.fcm.arrivon.flight.amadeus.client.search.invoker.ApiClient();
        apiClient.setBasePath(amadeusProperties.baseUrl());
        return apiClient;
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.search.api.ShoppingApi flightOffersSearchApi(
            com.fcm.arrivon.flight.amadeus.client.search.invoker.ApiClient flightSearchApiClient
    ) {
        return new com.fcm.arrivon.flight.amadeus.client.search.api.ShoppingApi(flightSearchApiClient);
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.price.invoker.ApiClient flightPricingApiClient() {
        com.fcm.arrivon.flight.amadeus.client.price.invoker.ApiClient apiClient =
                new com.fcm.arrivon.flight.amadeus.client.price.invoker.ApiClient();
        apiClient.setBasePath(amadeusProperties.baseUrl());
        return apiClient;
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.price.api.ShoppingApi flightOffersPriceApi(
            com.fcm.arrivon.flight.amadeus.client.price.invoker.ApiClient flightPricingApiClient
    ) {
        return new com.fcm.arrivon.flight.amadeus.client.price.api.ShoppingApi(flightPricingApiClient);
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.order.create.invoker.ApiClient flightOrderCreateApiClient() {
        com.fcm.arrivon.flight.amadeus.client.order.create.invoker.ApiClient apiClient =
                new com.fcm.arrivon.flight.amadeus.client.order.create.invoker.ApiClient();
        apiClient.setBasePath(amadeusProperties.baseUrl());
        return apiClient;
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.order.create.api.BookingApi flightCreateOrdersApi(
            com.fcm.arrivon.flight.amadeus.client.order.create.invoker.ApiClient flightOrderCreateApiClient
    ) {
        return new com.fcm.arrivon.flight.amadeus.client.order.create.api.BookingApi(flightOrderCreateApiClient);
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.order.management.invoker.ApiClient flightOrderManagementApiClient() {
        com.fcm.arrivon.flight.amadeus.client.order.management.invoker.ApiClient apiClient =
                new com.fcm.arrivon.flight.amadeus.client.order.management.invoker.ApiClient();
        apiClient.setBasePath(amadeusProperties.baseUrl());
        return apiClient;
    }

    @Bean
    public com.fcm.arrivon.flight.amadeus.client.order.management.api.BookingApi flightOrderManagementApi(
            com.fcm.arrivon.flight.amadeus.client.order.management.invoker.ApiClient flightOrderManagementApiClient
    ) {
        return new com.fcm.arrivon.flight.amadeus.client.order.management.api.BookingApi(flightOrderManagementApiClient);
    }
}
