package com.ebarbe.domain;

import static com.ebarbe.domain.HierarchyTestSamples.*;
import static com.ebarbe.domain.PersonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;

class HierarchyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hierarchy.class);
        Hierarchy hierarchy1 = getHierarchySample1();
        Hierarchy hierarchy2 = new Hierarchy();
        assertThat(hierarchy1).isNotEqualTo(hierarchy2);

        hierarchy2.setId(hierarchy1.getId());
        assertThat(hierarchy1).isEqualTo(hierarchy2);

        hierarchy2 = getHierarchySample2();
        assertThat(hierarchy1).isNotEqualTo(hierarchy2);
    }

    @Test
    void personTest() throws Exception {
        Hierarchy hierarchy = getHierarchyRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        hierarchy.setPersons((List<Person>) personBack);
        assertThat(hierarchy.getPersons()).isEqualTo(personBack);
        assertThat(personBack.getHierarchy()).isEqualTo(hierarchy);

        hierarchy.person(null);
        assertThat(hierarchy.getPersons()).isNull();
        assertThat(personBack.getHierarchy()).isNull();
    }
}
