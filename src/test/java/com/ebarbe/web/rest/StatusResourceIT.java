package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.MainTask;
import com.ebarbe.domain.Status;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.StatusRepository;
import com.ebarbe.repository.search.StatusSearchRepository;
import com.ebarbe.service.dto.StatusDTO;
import com.ebarbe.service.mapper.StatusMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link StatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatusResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/statuses/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private StatusMapper statusMapper;

    @Autowired
    private StatusSearchRepository statusSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatusMockMvc;

    private Status status;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity(EntityManager em) {
        Status status = new Status().description(DEFAULT_DESCRIPTION);
        return status;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createUpdatedEntity(EntityManager em) {
        Status status = new Status().description(UPDATED_DESCRIPTION);
        return status;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        statusSearchRepository.deleteAll();
        assertThat(statusSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        status = createEntity(em);
    }

    @Test
    @Transactional
    void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);
        restStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createStatusWithExistingId() throws Exception {
        // Create the Status with an existing ID
        status.setId(1L);
        StatusDTO statusDTO = statusMapper.toDto(status);

        int databaseSizeBeforeCreate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        // set the field null
        status.setDescription(null);

        // Create the Status, which fails.
        StatusDTO statusDTO = statusMapper.toDto(status);

        restStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getStatusesByIdFiltering() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        Long id = status.getId();

        defaultStatusShouldBeFound("id.equals=" + id);
        defaultStatusShouldNotBeFound("id.notEquals=" + id);

        defaultStatusShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStatusShouldNotBeFound("id.greaterThan=" + id);

        defaultStatusShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStatusShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where description equals to DEFAULT_DESCRIPTION
        defaultStatusShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the statusList where description equals to UPDATED_DESCRIPTION
        defaultStatusShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultStatusShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the statusList where description equals to UPDATED_DESCRIPTION
        defaultStatusShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where description is not null
        defaultStatusShouldBeFound("description.specified=true");

        // Get all the statusList where description is null
        defaultStatusShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where description contains DEFAULT_DESCRIPTION
        defaultStatusShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the statusList where description contains UPDATED_DESCRIPTION
        defaultStatusShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where description does not contain DEFAULT_DESCRIPTION
        defaultStatusShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the statusList where description does not contain UPDATED_DESCRIPTION
        defaultStatusShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStatusesByMainTaskIsEqualToSomething() throws Exception {
        MainTask mainTask;
        if (TestUtil.findAll(em, MainTask.class).isEmpty()) {
            statusRepository.saveAndFlush(status);
            mainTask = MainTaskResourceIT.createEntity(em);
        } else {
            mainTask = TestUtil.findAll(em, MainTask.class).get(0);
        }
        em.persist(mainTask);
        em.flush();
        status.addMainTask(mainTask);
        statusRepository.saveAndFlush(status);
        Long mainTaskId = mainTask.getId();
        // Get all the statusList where mainTask equals to mainTaskId
        defaultStatusShouldBeFound("mainTaskId.equals=" + mainTaskId);

        // Get all the statusList where mainTask equals to (mainTaskId + 1)
        defaultStatusShouldNotBeFound("mainTaskId.equals=" + (mainTaskId + 1));
    }

    @Test
    @Transactional
    void getAllStatusesBySubTaskIsEqualToSomething() throws Exception {
        SubTask subTask;
        if (TestUtil.findAll(em, SubTask.class).isEmpty()) {
            statusRepository.saveAndFlush(status);
            subTask = SubTaskResourceIT.createEntity(em);
        } else {
            subTask = TestUtil.findAll(em, SubTask.class).get(0);
        }
        em.persist(subTask);
        em.flush();
        status.addSubTask(subTask);
        statusRepository.saveAndFlush(status);
        Long subTaskId = subTask.getId();
        // Get all the statusList where subTask equals to subTaskId
        defaultStatusShouldBeFound("subTaskId.equals=" + subTaskId);

        // Get all the statusList where subTask equals to (subTaskId + 1)
        defaultStatusShouldNotBeFound("subTaskId.equals=" + (subTaskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStatusShouldBeFound(String filter) throws Exception {
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStatusShouldNotBeFound(String filter) throws Exception {
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        statusSearchRepository.save(status);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());

        // Update the status
        Status updatedStatus = statusRepository.findById(status.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus.description(UPDATED_DESCRIPTION);
        StatusDTO statusDTO = statusMapper.toDto(updatedStatus);

        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Status> statusSearchList = IterableUtils.toList(statusSearchRepository.findAll());
                Status testStatusSearch = statusSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testStatusSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        partialUpdatedStatus.description(UPDATED_DESCRIPTION);

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        status.setId(longCount.incrementAndGet());

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(statusDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusRepository.save(status);
        statusSearchRepository.save(status);

        int databaseSizeBeforeDelete = statusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the status
        restStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, status.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(statusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStatus() throws Exception {
        // Initialize the database
        status = statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);

        // Search the status
        restStatusMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
