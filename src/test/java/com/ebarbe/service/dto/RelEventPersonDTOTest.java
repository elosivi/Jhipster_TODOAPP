package com.ebarbe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RelEventPersonDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RelEventPersonDTO.class);
        RelEventPersonDTO relEventPersonDTO1 = new RelEventPersonDTO();
        relEventPersonDTO1.setId(1L);
        RelEventPersonDTO relEventPersonDTO2 = new RelEventPersonDTO();
        assertThat(relEventPersonDTO1).isNotEqualTo(relEventPersonDTO2);
        relEventPersonDTO2.setId(relEventPersonDTO1.getId());
        assertThat(relEventPersonDTO1).isEqualTo(relEventPersonDTO2);
        relEventPersonDTO2.setId(2L);
        assertThat(relEventPersonDTO1).isNotEqualTo(relEventPersonDTO2);
        relEventPersonDTO1.setId(null);
        assertThat(relEventPersonDTO1).isNotEqualTo(relEventPersonDTO2);
    }
}
