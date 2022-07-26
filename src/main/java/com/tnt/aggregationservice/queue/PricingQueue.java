package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfiguration;
import org.springframework.stereotype.Service;

@Service
public class PricingQueue extends GenericRequestQueue<Double> {
    public PricingQueue(ClientConfiguration config) {
        super(config);
    }
}
