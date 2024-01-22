package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Status getStatusSample1() {
        return new Status().id(1L).description("description1");
    }

    public static Status getStatusSample2() {
        return new Status().id(2L).description("description2");
    }

    public static Status getStatusRandomSampleGenerator() {
        return new Status().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
