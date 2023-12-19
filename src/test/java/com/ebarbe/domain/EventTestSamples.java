package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Event getEventSample1() {
        return new Event()
            .id(1L)
            .label("label1")
            .description("description1")
            .theme("theme1")
            .place("place1")
            .placeDetails("placeDetails1")
            .adress("adress1")
            .note("note1");
    }

    public static Event getEventSample2() {
        return new Event()
            .id(2L)
            .label("label2")
            .description("description2")
            .theme("theme2")
            .place("place2")
            .placeDetails("placeDetails2")
            .adress("adress2")
            .note("note2");
    }

    public static Event getEventRandomSampleGenerator() {
        return new Event()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .theme(UUID.randomUUID().toString())
            .place(UUID.randomUUID().toString())
            .placeDetails(UUID.randomUUID().toString())
            .adress(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
