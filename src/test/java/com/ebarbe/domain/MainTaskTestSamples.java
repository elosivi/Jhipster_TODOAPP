package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MainTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MainTask getMainTaskSample1() {
        return new MainTask().id(1L).description("description1");
    }

    public static MainTask getMainTaskSample2() {
        return new MainTask().id(2L).description("description2");
    }

    public static MainTask getMainTaskRandomSampleGenerator() {
        return new MainTask().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
