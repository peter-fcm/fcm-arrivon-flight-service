package com.fcm.arrivon.flight.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Amadeus Global Distribution System API integration.
 *
 * @param baseUrl amadeus gateway base URL
 * @param apiKey api client key / client id
 * @param apiSecret api client secret
 */
@ConfigurationProperties(prefix = "amadeus.api")
public record AmadeusProperties(
        String baseUrl,
        String apiKey,
        String apiSecret
) {
    public AmadeusProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://test.api.amadeus.com";
        }
    }
}
