package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import com.ebarbe.repository.search.SubTaskSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SubTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubTaskResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DEADLINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEADLINE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;

    private static final String ENTITY_API_URL = "/api/sub-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sub-tasks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired
    private SubTaskSearchRepository subTaskSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubTaskMockMvc;

    private SubTask subTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createEntity(EntityManager em) {
        SubTask subTask = new SubTask()
            .description(DEFAULT_DESCRIPTION)
            .deadline(DEFAULT_DEADLINE)
            .creation(DEFAULT_CREATION)
            .cost(DEFAULT_COST);
        return subTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubTask createUpdatedEntity(EntityManager em) {
        SubTask subTask = new SubTask()
            .description(UPDATED_DESCRIPTION)
            .deadline(UPDATED_DEADLINE)
            .creation(UPDATED_CREATION)
            .cost(UPDATED_COST);
        return subTask;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        subTaskSearchRepository.deleteAll();
        assertThat(subTaskSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        subTask = createEntity(em);
    }

    @Test
    @Transactional
    void createSubTask() throws Exception {
        int databaseSizeBeforeCreate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        // Create the SubTask
        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isCreated());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SubTask testSubTask = subTaskList.get(subTaskList.size() - 1);
        assertThat(testSubTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSubTask.getDeadline()).isEqualTo(DEFAULT_DEADLINE);
        assertThat(testSubTask.getCreation()).isEqualTo(DEFAULT_CREATION);
        assertThat(testSubTask.getCost()).isEqualTo(DEFAULT_COST);
    }

    @Test
    @Transactional
    void createSubTaskWithExistingId() throws Exception {
        // Create the SubTask with an existing ID
        subTask.setId(1L);

        int databaseSizeBeforeCreate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        // set the field null
        subTask.setDescription(null);

        // Create the SubTask, which fails.

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isBadRequest());

        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDeadlineIsRequired() throws Exception {
        int databaseSizeBeforeTest = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        // set the field null
        subTask.setDeadline(null);

        // Create the SubTask, which fails.

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isBadRequest());

        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSubTasks() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));
    }

    @Test
    @Transactional
    void getSubTask() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get the subTask
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, subTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subTask.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.deadline").value(DEFAULT_DEADLINE.toString()))
            .andExpect(jsonPath("$.creation").value(DEFAULT_CREATION.toString()))
            .andExpect(jsonPath("$.cost").value(DEFAULT_COST.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingSubTask() throws Exception {
        // Get the subTask
        restSubTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubTask() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        subTaskSearchRepository.save(subTask);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());

        // Update the subTask
        SubTask updatedSubTask = subTaskRepository.findById(subTask.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubTask are not directly saved in db
        em.detach(updatedSubTask);
        updatedSubTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION).cost(UPDATED_COST);

        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSubTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSubTask))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        SubTask testSubTask = subTaskList.get(subTaskList.size() - 1);
        assertThat(testSubTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testSubTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testSubTask.getCost()).isEqualTo(UPDATED_COST);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SubTask> subTaskSearchList = IterableUtils.toList(subTaskSearchRepository.findAll());
                SubTask testSubTaskSearch = subTaskSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSubTaskSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSubTaskSearch.getDeadline()).isEqualTo(UPDATED_DEADLINE);
                assertThat(testSubTaskSearch.getCreation()).isEqualTo(UPDATED_CREATION);
                assertThat(testSubTaskSearch.getCost()).isEqualTo(UPDATED_COST);
            });
    }

    @Test
    @Transactional
    void putNonExistingSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subTask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION);

        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubTask))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        SubTask testSubTask = subTaskList.get(subTaskList.size() - 1);
        assertThat(testSubTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testSubTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testSubTask.getCost()).isEqualTo(DEFAULT_COST);
    }

    @Test
    @Transactional
    void fullUpdateSubTaskWithPatch() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();

        // Update the subTask using partial update
        SubTask partialUpdatedSubTask = new SubTask();
        partialUpdatedSubTask.setId(subTask.getId());

        partialUpdatedSubTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION).cost(UPDATED_COST);

        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubTask))
            )
            .andExpect(status().isOk());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        SubTask testSubTask = subTaskList.get(subTaskList.size() - 1);
        assertThat(testSubTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testSubTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testSubTask.getCost()).isEqualTo(UPDATED_COST);
    }

    @Test
    @Transactional
    void patchNonExistingSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subTask))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubTask() throws Exception {
        int databaseSizeBeforeUpdate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        subTask.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(subTask)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubTask in the database
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSubTask() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);
        subTaskRepository.save(subTask);
        subTaskSearchRepository.save(subTask);

        int databaseSizeBeforeDelete = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subTask
        restSubTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, subTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SubTask> subTaskList = subTaskRepository.findAll();
        assertThat(subTaskList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSubTask() throws Exception {
        // Initialize the database
        subTask = subTaskRepository.saveAndFlush(subTask);
        subTaskSearchRepository.save(subTask);

        // Search the subTask
        restSubTaskMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + subTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));
    }
}
