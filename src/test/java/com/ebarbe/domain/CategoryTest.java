package com.ebarbe.domain;

import static com.ebarbe.domain.CategoryTestSamples.*;
import static com.ebarbe.domain.MainTaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ebarbe.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void mainTaskTest() throws Exception {
        Category category = getCategoryRandomSampleGenerator();
        MainTask mainTaskBack = getMainTaskRandomSampleGenerator();

        category.addMainTask(mainTaskBack);
        assertThat(category.getMainTasks()).containsOnly(mainTaskBack);
        assertThat(mainTaskBack.getCategory()).isEqualTo(category);

        category.removeMainTask(mainTaskBack);
        assertThat(category.getMainTasks()).doesNotContain(mainTaskBack);
        assertThat(mainTaskBack.getCategory()).isNull();

        category.mainTasks(new HashSet<>(Set.of(mainTaskBack)));
        assertThat(category.getMainTasks()).containsOnly(mainTaskBack);
        assertThat(mainTaskBack.getCategory()).isEqualTo(category);

        category.setMainTasks(new HashSet<>());
        assertThat(category.getMainTasks()).doesNotContain(mainTaskBack);
        assertThat(mainTaskBack.getCategory()).isNull();
    }
}
