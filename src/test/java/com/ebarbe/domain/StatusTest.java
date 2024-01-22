package com.ebarbe.domain;

import static com.ebarbe.domain.MainTaskTestSamples.*;
import static com.ebarbe.domain.StatusTestSamples.*;
import static com.ebarbe.domain.SubTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

        status.addMainTask(mainTaskBack);
        assertThat(status.getMainTasks()).containsOnly(mainTaskBack);
        assertThat(mainTaskBack.getStatus()).isEqualTo(status);

        status.removeMainTask(mainTaskBack);
        assertThat(status.getMainTasks()).doesNotContain(mainTaskBack);
        assertThat(mainTaskBack.getStatus()).isNull();

        status.mainTasks(new HashSet<>(Set.of(mainTaskBack)));
        assertThat(status.getMainTasks()).containsOnly(mainTaskBack);
        assertThat(mainTaskBack.getStatus()).isEqualTo(status);

        status.setMainTasks(new HashSet<>());
        assertThat(status.getMainTasks()).doesNotContain(mainTaskBack);
        assertThat(mainTaskBack.getStatus()).isNull();
    }

    @Test
    void subTaskTest() throws Exception {
        Status status = getStatusRandomSampleGenerator();
        SubTask subTaskBack = getSubTaskRandomSampleGenerator();

        status.addSubTask(subTaskBack);
        assertThat(status.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getStatus()).isEqualTo(status);

        status.removeSubTask(subTaskBack);
        assertThat(status.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getStatus()).isNull();

        status.subTasks(new HashSet<>(Set.of(subTaskBack)));
        assertThat(status.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getStatus()).isEqualTo(status);

        status.setSubTasks(new HashSet<>());
        assertThat(status.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getStatus()).isNull();
    }
}
