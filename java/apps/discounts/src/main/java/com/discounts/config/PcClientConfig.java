package com.discounts.config;

import com.pc_client.impl.PcClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PcClientConfig {

    @Value(value = "${pc-base-url}")
    private String pcBaseUrl;

    @Value(value = "${security.pc-api-key}")
    private String API_KEY;

    @Bean
    public PcClientImpl createClient() {
        return new PcClientImpl(pcBaseUrl, API_KEY);
    }

}
