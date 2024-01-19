package com.ebarbe.domain;

import static com.ebarbe.domain.EventTestSamples.*;
import static com.ebarbe.domain.EventTypeTestSamples.*;
import static com.ebarbe.domain.PersonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void eventTypeTest() throws Exception {
        Event event = getEventRandomSampleGenerator();
        EventType eventTypeBack = getEventTypeRandomSampleGenerator();

        event.setEventType(eventTypeBack);
        assertThat(event.getEventType()).isEqualTo(eventTypeBack);

        event.eventType(null);
        assertThat(event.getEventType()).isNull();
    }

    @Test
    void personTest() throws Exception {
        Event event = getEventRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        event.addPerson(personBack);
        assertThat(event.getPeople()).containsOnly(personBack);

        event.removePerson(personBack);
        assertThat(event.getPeople()).doesNotContain(personBack);

        event.person(new HashSet<>(Set.of(personBack)));
        assertThat(event.getPeople()).containsOnly(personBack);

        event.setPeople(new HashSet<>());
        assertThat(event.getPeople()).doesNotContain(personBack);
    }
}
