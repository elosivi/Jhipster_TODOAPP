package com.ebarbe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubTaskDTO.class);
        SubTaskDTO subTaskDTO1 = new SubTaskDTO();
        subTaskDTO1.setId(1L);
        SubTaskDTO subTaskDTO2 = new SubTaskDTO();
        assertThat(subTaskDTO1).isNotEqualTo(subTaskDTO2);
        subTaskDTO2.setId(subTaskDTO1.getId());
        assertThat(subTaskDTO1).isEqualTo(subTaskDTO2);
        subTaskDTO2.setId(2L);
        assertThat(subTaskDTO1).isNotEqualTo(subTaskDTO2);
        subTaskDTO1.setId(null);
        assertThat(subTaskDTO1).isNotEqualTo(subTaskDTO2);
    }
}
