package com.ebarbe.domain;

import static com.ebarbe.domain.EventTestSamples.*;
import static com.ebarbe.domain.EventTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventType.class);
        EventType eventType1 = getEventTypeSample1();
        EventType eventType2 = new EventType();
        assertThat(eventType1).isNotEqualTo(eventType2);

        eventType2.setId(eventType1.getId());
        assertThat(eventType1).isEqualTo(eventType2);

        eventType2 = getEventTypeSample2();
        assertThat(eventType1).isNotEqualTo(eventType2);
    }

    @Test
    void eventTest() throws Exception {
        EventType eventType = getEventTypeRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        eventType.setEvents((Set<Event>) eventBack);
        assertThat(eventType.getEvents()).isEqualTo(eventBack);
        assertThat(eventBack.getEventType()).isEqualTo(eventType);

        eventType.events(null);
        assertThat(eventType.getEvents()).isNull();
        assertThat(eventBack.getEventType()).isNull();
    }
}
