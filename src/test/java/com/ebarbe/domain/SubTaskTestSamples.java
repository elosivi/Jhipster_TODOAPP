package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SubTask getSubTaskSample1() {
        return new SubTask().id(1L).description("description1");
    }

    public static SubTask getSubTaskSample2() {
        return new SubTask().id(2L).description("description2");
    }

    public static SubTask getSubTaskRandomSampleGenerator() {
        return new SubTask().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
