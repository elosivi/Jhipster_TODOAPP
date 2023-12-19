package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.EventType;
import com.ebarbe.repository.EventTypeRepository;
import com.ebarbe.repository.search.EventTypeSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.Duration;
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
 * Integration tests for the {@link EventTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EventTypeResourceIT {

    private static final String DEFAULT_LABEL = "Fmzblm0";
    private static final String UPDATED_LABEL = "Fnzayl0";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    private static final String ENTITY_API_URL = "/api/event-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/event-types/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeSearchRepository eventTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventTypeMockMvc;

    private EventType eventType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventType createEntity(EntityManager em) {
        EventType eventType = new EventType().label(DEFAULT_LABEL).description(DEFAULT_DESCRIPTION).duration(DEFAULT_DURATION);
        return eventType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventType createUpdatedEntity(EntityManager em) {
        EventType eventType = new EventType().label(UPDATED_LABEL).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION);
        return eventType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        eventTypeSearchRepository.deleteAll();
        assertThat(eventTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        eventType = createEntity(em);
    }

    @Test
    @Transactional
    void createEventType() throws Exception {
        int databaseSizeBeforeCreate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        // Create the EventType
        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventType)))
            .andExpect(status().isCreated());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testEventType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEventType.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void createEventTypeWithExistingId() throws Exception {
        // Create the EventType with an existing ID
        eventType.setId(1L);

        int databaseSizeBeforeCreate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventType)))
            .andExpect(status().isBadRequest());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        // set the field null
        eventType.setLabel(null);

        // Create the EventType, which fails.

        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventType)))
            .andExpect(status().isBadRequest());

        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEventTypes() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventType.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));
    }

    @Test
    @Transactional
    void getEventType() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get the eventType
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, eventType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventType.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEventType() throws Exception {
        // Get the eventType
        restEventTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventType() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        eventTypeSearchRepository.save(eventType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());

        // Update the eventType
        EventType updatedEventType = eventTypeRepository.findById(eventType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventType are not directly saved in db
        em.detach(updatedEventType);
        updatedEventType.label(UPDATED_LABEL).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION);

        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEventType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEventType))
            )
            .andExpect(status().isOk());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testEventType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEventType.getDuration()).isEqualTo(UPDATED_DURATION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EventType> eventTypeSearchList = IterableUtils.toList(eventTypeSearchRepository.findAll());
                EventType testEventTypeSearch = eventTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEventTypeSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testEventTypeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testEventTypeSearch.getDuration()).isEqualTo(UPDATED_DURATION);
            });
    }

    @Test
    @Transactional
    void putNonExistingEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventType)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEventTypeWithPatch() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();

        // Update the eventType using partial update
        EventType partialUpdatedEventType = new EventType();
        partialUpdatedEventType.setId(eventType.getId());

        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEventType))
            )
            .andExpect(status().isOk());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testEventType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEventType.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void fullUpdateEventTypeWithPatch() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();

        // Update the eventType using partial update
        EventType partialUpdatedEventType = new EventType();
        partialUpdatedEventType.setId(eventType.getId());

        partialUpdatedEventType.label(UPDATED_LABEL).description(UPDATED_DESCRIPTION).duration(UPDATED_DURATION);

        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEventType))
            )
            .andExpect(status().isOk());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        EventType testEventType = eventTypeList.get(eventTypeList.size() - 1);
        assertThat(testEventType.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testEventType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEventType.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    @Transactional
    void patchNonExistingEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventType))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventType() throws Exception {
        int databaseSizeBeforeUpdate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        eventType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(eventType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventType in the database
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEventType() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);
        eventTypeRepository.save(eventType);
        eventTypeSearchRepository.save(eventType);

        int databaseSizeBeforeDelete = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the eventType
        restEventTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EventType> eventTypeList = eventTypeRepository.findAll();
        assertThat(eventTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEventType() throws Exception {
        // Initialize the database
        eventType = eventTypeRepository.saveAndFlush(eventType);
        eventTypeSearchRepository.save(eventType);

        // Search the eventType
        restEventTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + eventType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventType.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));
    }
}
