package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PersonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Person getPersonSample1() {
        return new Person().id(1L).description("description1").pseudo("pseudo1").name("name1");
    }

    public static Person getPersonSample2() {
        return new Person().id(2L).description("description2").pseudo("pseudo2").name("name2");
    }

    public static Person getPersonRandomSampleGenerator() {
        return new Person()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .pseudo(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString());
    }
}
