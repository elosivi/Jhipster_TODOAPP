package com.ebarbe.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    public void setUp() {
        eventMapper = new EventMapperImpl();
    }
}
