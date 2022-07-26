package com.tnt.aggregationservice.queue;

import com.tnt.aggregationservice.config.ClientConfiguration;
import org.springframework.stereotype.Service;

@Service
public class TrackingQueue extends GenericRequestQueue<String> {
    public TrackingQueue(ClientConfiguration config) {
        super(config);
    }
}
