package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingQueue extends GenericRequestQueue<List<String>> {
    public ShippingQueue(ClientConfiguration config) {
        super(config);
    }
}
