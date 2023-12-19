package com.ebarbe.domain;

import static com.ebarbe.domain.CategoryTestSamples.*;
import static com.ebarbe.domain.MainTaskTestSamples.*;
import static com.ebarbe.domain.PersonTestSamples.*;
import static com.ebarbe.domain.StatusTestSamples.*;
import static com.ebarbe.domain.SubTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MainTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MainTask.class);
        MainTask mainTask1 = getMainTaskSample1();
        MainTask mainTask2 = new MainTask();
        assertThat(mainTask1).isNotEqualTo(mainTask2);

        mainTask2.setId(mainTask1.getId());
        assertThat(mainTask1).isEqualTo(mainTask2);

        mainTask2 = getMainTaskSample2();
        assertThat(mainTask1).isNotEqualTo(mainTask2);
    }

    @Test
    void statusTest() throws Exception {
        MainTask mainTask = getMainTaskRandomSampleGenerator();
        Status statusBack = getStatusRandomSampleGenerator();

        mainTask.setStatus(statusBack);
        assertThat(mainTask.getStatus()).isEqualTo(statusBack);

        mainTask.status(null);
        assertThat(mainTask.getStatus()).isNull();
    }

    @Test
    void categoryTest() throws Exception {
        MainTask mainTask = getMainTaskRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        mainTask.setCategory(categoryBack);
        assertThat(mainTask.getCategory()).isEqualTo(categoryBack);

        mainTask.category(null);
        assertThat(mainTask.getCategory()).isNull();
    }

    @Test
    void personOwnerTest() throws Exception {
        MainTask mainTask = getMainTaskRandomSampleGenerator();
        Person personBack = getPersonRandomSampleGenerator();

        mainTask.setPersonOwner(personBack);
        assertThat(mainTask.getPersonOwner()).isEqualTo(personBack);

        mainTask.personOwner(null);
        assertThat(mainTask.getPersonOwner()).isNull();
    }

    @Test
    void subTaskTest() throws Exception {
        MainTask mainTask = getMainTaskRandomSampleGenerator();
        SubTask subTaskBack = getSubTaskRandomSampleGenerator();

        mainTask.addSubTask(subTaskBack);
        assertThat(mainTask.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getMainTask()).isEqualTo(mainTask);

        mainTask.removeSubTask(subTaskBack);
        assertThat(mainTask.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getMainTask()).isNull();

        mainTask.subTasks(new HashSet<>(Set.of(subTaskBack)));
        assertThat(mainTask.getSubTasks()).containsOnly(subTaskBack);
        assertThat(subTaskBack.getMainTask()).isEqualTo(mainTask);

        mainTask.setSubTasks(new HashSet<>());
        assertThat(mainTask.getSubTasks()).doesNotContain(subTaskBack);
        assertThat(subTaskBack.getMainTask()).isNull();
    }
}
