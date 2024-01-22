package com.ebarbe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HierarchyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HierarchyDTO.class);
        HierarchyDTO hierarchyDTO1 = new HierarchyDTO();
        hierarchyDTO1.setId(1L);
        HierarchyDTO hierarchyDTO2 = new HierarchyDTO();
        assertThat(hierarchyDTO1).isNotEqualTo(hierarchyDTO2);
        hierarchyDTO2.setId(hierarchyDTO1.getId());
        assertThat(hierarchyDTO1).isEqualTo(hierarchyDTO2);
        hierarchyDTO2.setId(2L);
        assertThat(hierarchyDTO1).isNotEqualTo(hierarchyDTO2);
        hierarchyDTO1.setId(null);
        assertThat(hierarchyDTO1).isNotEqualTo(hierarchyDTO2);
    }
}
