package com.ebarbe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MainTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MainTaskDTO.class);
        MainTaskDTO mainTaskDTO1 = new MainTaskDTO();
        mainTaskDTO1.setId(1L);
        MainTaskDTO mainTaskDTO2 = new MainTaskDTO();
        assertThat(mainTaskDTO1).isNotEqualTo(mainTaskDTO2);
        mainTaskDTO2.setId(mainTaskDTO1.getId());
        assertThat(mainTaskDTO1).isEqualTo(mainTaskDTO2);
        mainTaskDTO2.setId(2L);
        assertThat(mainTaskDTO1).isNotEqualTo(mainTaskDTO2);
        mainTaskDTO1.setId(null);
        assertThat(mainTaskDTO1).isNotEqualTo(mainTaskDTO2);
    }
}
