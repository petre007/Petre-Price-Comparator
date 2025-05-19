package com.market.config;

import com.pc_client.impl.PcClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PcClientConfig {

    private String pcBaseUrl;

    private String API_KEY;

    @Bean
    public PcClientImpl createClient() {
        return new PcClientImpl(pcBaseUrl, API_KEY);
    }

}
