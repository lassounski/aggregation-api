package com.tnt.aggregationservice.config;

import com.tnt.aggregationservice.model.PricingResult;
import com.tnt.aggregationservice.model.ShippingResult;
import com.tnt.aggregationservice.model.TrackingResult;
import com.tnt.aggregationservice.queue.PricingQueue;
import com.tnt.aggregationservice.queue.ShippingQueue;
import com.tnt.aggregationservice.queue.TrackingQueue;
import com.tnt.aggregationservice.service.QueryService;
import com.tnt.aggregationservice.webclient.NonBlockingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueryServiceConfiguration {

    private final ClientConfiguration clientConfiguration;

    @Bean
    QueryService<PricingResult> pricingService(final NonBlockingClient pricingWebClient,
                                               final PricingQueue pricingQueue) {
        return new QueryService<>(
                clientConfiguration,
                pricingWebClient,
                pricingQueue,
                () -> new PricingResult()
        );
    }

    @Bean
    QueryService<ShippingResult> shippingService(final NonBlockingClient shippingWebClient,
                                                final ShippingQueue shippingQueue) {
        return new QueryService<>(
                clientConfiguration,
                shippingWebClient,
                shippingQueue,
                () -> new ShippingResult()
        );
    }

    @Bean
    QueryService<TrackingResult> trackingService(final NonBlockingClient trackingWebClient,
                                                final TrackingQueue trackingQueue) {
        return new QueryService<>(
                clientConfiguration,
                trackingWebClient,
                trackingQueue,
                () -> new TrackingResult()
        );
    }
}
