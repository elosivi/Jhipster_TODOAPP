package com.ebarbe.domain;

import static com.ebarbe.domain.HierarchyTestSamples.*;
import static com.ebarbe.domain.RelEventPersonTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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
}
