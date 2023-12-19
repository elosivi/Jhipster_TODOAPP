package com.ebarbe.domain;

import static com.ebarbe.domain.EventTestSamples.*;
import static com.ebarbe.domain.HierarchyTestSamples.*;
import static com.ebarbe.domain.PersonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PersonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Person.class);
        Person person1 = getPersonSample1();
        Person person2 = new Person();
        assertThat(person1).isNotEqualTo(person2);

        person2.setId(person1.getId());
        assertThat(person1).isEqualTo(person2);

        person2 = getPersonSample2();
        assertThat(person1).isNotEqualTo(person2);
    }

    @Test
    void hierarchyTest() throws Exception {
        Person person = getPersonRandomSampleGenerator();
        Hierarchy hierarchyBack = getHierarchyRandomSampleGenerator();

        person.setHierarchy(hierarchyBack);
        assertThat(person.getHierarchy()).isEqualTo(hierarchyBack);

        person.hierarchy(null);
        assertThat(person.getHierarchy()).isNull();
    }

    @Test
    void eventTest() throws Exception {
        Person person = getPersonRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        person.addEvent(eventBack);
        assertThat(person.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getPeople()).containsOnly(person);

        person.removeEvent(eventBack);
        assertThat(person.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getPeople()).doesNotContain(person);

        person.events(new HashSet<>(Set.of(eventBack)));
        assertThat(person.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getPeople()).containsOnly(person);

        person.setEvents(new HashSet<>());
        assertThat(person.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getPeople()).doesNotContain(person);
    }
}
