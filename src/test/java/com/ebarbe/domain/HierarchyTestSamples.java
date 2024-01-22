package com.ebarbe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HierarchyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Hierarchy getHierarchySample1() {
        return new Hierarchy().id(1L).description("description1");
    }

    public static Hierarchy getHierarchySample2() {
        return new Hierarchy().id(2L).description("description2");
    }

    public static Hierarchy getHierarchyRandomSampleGenerator() {
        return new Hierarchy().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
