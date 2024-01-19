package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.Event;
import com.ebarbe.domain.EventType;
import com.ebarbe.repository.EventTypeRepository;
import com.ebarbe.repository.search.EventTypeSearchRepository;
import com.ebarbe.service.dto.EventTypeDTO;
import com.ebarbe.service.mapper.EventTypeMapper;
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

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);
    private static final Duration SMALLER_DURATION = Duration.ofHours(5);

    private static final String ENTITY_API_URL = "/api/event-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/event-types/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeMapper eventTypeMapper;

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
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);
        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
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
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        int databaseSizeBeforeCreate = eventTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
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
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        restEventTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
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
    void getEventTypesByIdFiltering() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        Long id = eventType.getId();

        defaultEventTypeShouldBeFound("id.equals=" + id);
        defaultEventTypeShouldNotBeFound("id.notEquals=" + id);

        defaultEventTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultEventTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventTypesByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where label equals to DEFAULT_LABEL
        defaultEventTypeShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the eventTypeList where label equals to UPDATED_LABEL
        defaultEventTypeShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventTypesByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultEventTypeShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the eventTypeList where label equals to UPDATED_LABEL
        defaultEventTypeShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventTypesByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where label is not null
        defaultEventTypeShouldBeFound("label.specified=true");

        // Get all the eventTypeList where label is null
        defaultEventTypeShouldNotBeFound("label.specified=false");
    }

    @Test
    @Transactional
    void getAllEventTypesByLabelContainsSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where label contains DEFAULT_LABEL
        defaultEventTypeShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the eventTypeList where label contains UPDATED_LABEL
        defaultEventTypeShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventTypesByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where label does not contain DEFAULT_LABEL
        defaultEventTypeShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the eventTypeList where label does not contain UPDATED_LABEL
        defaultEventTypeShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventTypesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where description equals to DEFAULT_DESCRIPTION
        defaultEventTypeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the eventTypeList where description equals to UPDATED_DESCRIPTION
        defaultEventTypeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultEventTypeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the eventTypeList where description equals to UPDATED_DESCRIPTION
        defaultEventTypeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where description is not null
        defaultEventTypeShouldBeFound("description.specified=true");

        // Get all the eventTypeList where description is null
        defaultEventTypeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllEventTypesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where description contains DEFAULT_DESCRIPTION
        defaultEventTypeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the eventTypeList where description contains UPDATED_DESCRIPTION
        defaultEventTypeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where description does not contain DEFAULT_DESCRIPTION
        defaultEventTypeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the eventTypeList where description does not contain UPDATED_DESCRIPTION
        defaultEventTypeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration equals to DEFAULT_DURATION
        defaultEventTypeShouldBeFound("duration.equals=" + DEFAULT_DURATION);

        // Get all the eventTypeList where duration equals to UPDATED_DURATION
        defaultEventTypeShouldNotBeFound("duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration in DEFAULT_DURATION or UPDATED_DURATION
        defaultEventTypeShouldBeFound("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION);

        // Get all the eventTypeList where duration equals to UPDATED_DURATION
        defaultEventTypeShouldNotBeFound("duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration is not null
        defaultEventTypeShouldBeFound("duration.specified=true");

        // Get all the eventTypeList where duration is null
        defaultEventTypeShouldNotBeFound("duration.specified=false");
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration is greater than or equal to DEFAULT_DURATION
        defaultEventTypeShouldBeFound("duration.greaterThanOrEqual=" + DEFAULT_DURATION);

        // Get all the eventTypeList where duration is greater than or equal to UPDATED_DURATION
        defaultEventTypeShouldNotBeFound("duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration is less than or equal to DEFAULT_DURATION
        defaultEventTypeShouldBeFound("duration.lessThanOrEqual=" + DEFAULT_DURATION);

        // Get all the eventTypeList where duration is less than or equal to SMALLER_DURATION
        defaultEventTypeShouldNotBeFound("duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration is less than DEFAULT_DURATION
        defaultEventTypeShouldNotBeFound("duration.lessThan=" + DEFAULT_DURATION);

        // Get all the eventTypeList where duration is less than UPDATED_DURATION
        defaultEventTypeShouldBeFound("duration.lessThan=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventTypeRepository.saveAndFlush(eventType);

        // Get all the eventTypeList where duration is greater than DEFAULT_DURATION
        defaultEventTypeShouldNotBeFound("duration.greaterThan=" + DEFAULT_DURATION);

        // Get all the eventTypeList where duration is greater than SMALLER_DURATION
        defaultEventTypeShouldBeFound("duration.greaterThan=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllEventTypesByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            eventTypeRepository.saveAndFlush(eventType);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        eventType.addEvent(event);
        eventTypeRepository.saveAndFlush(eventType);
        Long eventId = event.getId();
        // Get all the eventTypeList where event equals to eventId
        defaultEventTypeShouldBeFound("eventId.equals=" + eventId);

        // Get all the eventTypeList where event equals to (eventId + 1)
        defaultEventTypeShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventTypeShouldBeFound(String filter) throws Exception {
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventType.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));

        // Check, that the count call also returns 1
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventTypeShouldNotBeFound(String filter) throws Exception {
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(updatedEventType);

        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventTypeDTO)))
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

        partialUpdatedEventType.label(UPDATED_LABEL).duration(UPDATED_DURATION);

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
        assertThat(testEventType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEventType.getDuration()).isEqualTo(UPDATED_DURATION);
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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

        // Create the EventType
        EventTypeDTO eventTypeDTO = eventTypeMapper.toDto(eventType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(eventTypeDTO))
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
