package com.ebarbe.domain;

import static com.ebarbe.domain.MainTaskTestSamples.*;
import static com.ebarbe.domain.PersonTestSamples.*;
import static com.ebarbe.domain.StatusTestSamples.*;
import static com.ebarbe.domain.SubTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubTask.class);
        SubTask subTask1 = getSubTaskSample1();
        SubTask subTask2 = new SubTask();
        assertThat(subTask1).isNotEqualTo(subTask2);

        subTask2.setId(subTask1.getId());
        assertThat(subTask1).isEqualTo(subTask2);

        subTask2 = getSubTaskSample2();
        assertThat(subTask1).isNotEqualTo(subTask2);
    }

    @Test
    void statusTest() throws Exception {
        SubTask subTask = getSubTaskRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        subTask.setStatus(statusBack);
        assertThat(subTask.getStatus()).isEqualTo(statusBack);

        subTask.status(null);
        assertThat(subTask.getStatus()).isNull();
    }

    @Test
    void mainTaskTest() throws Exception {
        SubTask subTask = getSubTaskRandomSampleGenerator();
        MainTask mainTaskBack = getMainTaskRandomSampleGenerator();

        subTask.setMainTask(mainTaskBack);
        assertThat(subTask.getMainTask()).isEqualTo(mainTaskBack);

        subTask.mainTask(null);
        assertThat(subTask.getMainTask()).isNull();
    }

    @Test
    void personDoerTest() throws Exception {
        SubTask subTask = getSubTaskRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        subTask.setPersonDoer(personBack);
        assertThat(subTask.getPersonDoer()).isEqualTo(personBack);

        subTask.personDoer(null);
        assertThat(subTask.getPersonDoer()).isNull();
    }
}
