package com.ebarbe.domain;

import static com.ebarbe.domain.MainTaskTestSamples.*;
import static com.ebarbe.domain.StatusTestSamples.*;
import static com.ebarbe.domain.SubTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatusTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Status.class);
        Status status1 = getStatusSample1();
        Status status2 = new Status();
        assertThat(status1).isNotEqualTo(status2);

        status2.setId(status1.getId());
        assertThat(status1).isEqualTo(status2);

        status2 = getStatusSample2();
        assertThat(status1).isNotEqualTo(status2);
    }

    @Test
    void mainTaskTest() throws Exception {
        Status status = getStatusRandomSampleGenerator();
        MainTask mainTaskBack = getMainTaskRandomSampleGenerator();

        status.setMainTask(mainTaskBack);
        assertThat(status.getMainTask()).isEqualTo(mainTaskBack);
        assertThat(mainTaskBack.getStatus()).isEqualTo(status);

        status.mainTask(null);
        assertThat(status.getMainTask()).isNull();
        assertThat(mainTaskBack.getStatus()).isNull();
    }

    @Test
    void subTaskTest() throws Exception {
        Status status = getStatusRandomSampleGenerator();
        SubTask subTaskBack = getSubTaskRandomSampleGenerator();

        status.setSubTask(subTaskBack);
        assertThat(status.getSubTask()).isEqualTo(subTaskBack);
        assertThat(subTaskBack.getStatus()).isEqualTo(status);

        status.subTask(null);
        assertThat(status.getSubTask()).isNull();
        assertThat(subTaskBack.getStatus()).isNull();
    }
}
