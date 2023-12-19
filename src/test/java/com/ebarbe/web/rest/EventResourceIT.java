package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.Event;
import com.ebarbe.repository.EventRepository;
import com.ebarbe.repository.search.EventSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventResourceIT {

    private static final String DEFAULT_LABEL = "Vvqcf2";
    private static final String UPDATED_LABEL = "Pkquv5";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THEME = "AAAAAAAAAA";
    private static final String UPDATED_THEME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_END = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PLACE = "AAAAAAAAAA";
    private static final String UPDATED_PLACE = "BBBBBBBBBB";

    private static final String DEFAULT_PLACE_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_PLACE_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADRESS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/events/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private EventRepository eventRepositoryMock;

    @Autowired
    private EventSearchRepository eventSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventMockMvc;

    private Event event;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
            .label(DEFAULT_LABEL)
            .description(DEFAULT_DESCRIPTION)
            .theme(DEFAULT_THEME)
            .dateStart(DEFAULT_DATE_START)
            .dateEnd(DEFAULT_DATE_END)
            .place(DEFAULT_PLACE)
            .placeDetails(DEFAULT_PLACE_DETAILS)
            .adress(DEFAULT_ADRESS)
            .note(DEFAULT_NOTE);
        return event;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createUpdatedEntity(EntityManager em) {
        Event event = new Event()
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .theme(UPDATED_THEME)
            .dateStart(UPDATED_DATE_START)
            .dateEnd(UPDATED_DATE_END)
            .place(UPDATED_PLACE)
            .placeDetails(UPDATED_PLACE_DETAILS)
            .adress(UPDATED_ADRESS)
            .note(UPDATED_NOTE);
        return event;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        eventSearchRepository.deleteAll();
        assertThat(eventSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        event = createEntity(em);
    }

    @Test
    @Transactional
    void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // Create the Event
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEvent.getTheme()).isEqualTo(DEFAULT_THEME);
        assertThat(testEvent.getDateStart()).isEqualTo(DEFAULT_DATE_START);
        assertThat(testEvent.getDateEnd()).isEqualTo(DEFAULT_DATE_END);
        assertThat(testEvent.getPlace()).isEqualTo(DEFAULT_PLACE);
        assertThat(testEvent.getPlaceDetails()).isEqualTo(DEFAULT_PLACE_DETAILS);
        assertThat(testEvent.getAdress()).isEqualTo(DEFAULT_ADRESS);
        assertThat(testEvent.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void createEventWithExistingId() throws Exception {
        // Create the Event with an existing ID
        event.setId(1L);

        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setLabel(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setDateStart(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setDateEnd(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPlaceIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        // set the field null
        event.setPlace(null);

        // Create the Event, which fails.

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isBadRequest());

        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].theme").value(hasItem(DEFAULT_THEME)))
            .andExpect(jsonPath("$.[*].dateStart").value(hasItem(DEFAULT_DATE_START.toString())))
            .andExpect(jsonPath("$.[*].dateEnd").value(hasItem(DEFAULT_DATE_END.toString())))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE)))
            .andExpect(jsonPath("$.[*].placeDetails").value(hasItem(DEFAULT_PLACE_DETAILS)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc
            .perform(get(ENTITY_API_URL_ID, event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.theme").value(DEFAULT_THEME))
            .andExpect(jsonPath("$.dateStart").value(DEFAULT_DATE_START.toString()))
            .andExpect(jsonPath("$.dateEnd").value(DEFAULT_DATE_END.toString()))
            .andExpect(jsonPath("$.place").value(DEFAULT_PLACE))
            .andExpect(jsonPath("$.placeDetails").value(DEFAULT_PLACE_DETAILS))
            .andExpect(jsonPath("$.adress").value(DEFAULT_ADRESS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        eventSearchRepository.save(event);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());

        // Update the event
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvent are not directly saved in db
        em.detach(updatedEvent);
        updatedEvent
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .theme(UPDATED_THEME)
            .dateStart(UPDATED_DATE_START)
            .dateEnd(UPDATED_DATE_END)
            .place(UPDATED_PLACE)
            .placeDetails(UPDATED_PLACE_DETAILS)
            .adress(UPDATED_ADRESS)
            .note(UPDATED_NOTE);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getTheme()).isEqualTo(UPDATED_THEME);
        assertThat(testEvent.getDateStart()).isEqualTo(UPDATED_DATE_START);
        assertThat(testEvent.getDateEnd()).isEqualTo(UPDATED_DATE_END);
        assertThat(testEvent.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testEvent.getPlaceDetails()).isEqualTo(UPDATED_PLACE_DETAILS);
        assertThat(testEvent.getAdress()).isEqualTo(UPDATED_ADRESS);
        assertThat(testEvent.getNote()).isEqualTo(UPDATED_NOTE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Event> eventSearchList = IterableUtils.toList(eventSearchRepository.findAll());
                Event testEventSearch = eventSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEventSearch.getLabel()).isEqualTo(UPDATED_LABEL);
                assertThat(testEventSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testEventSearch.getTheme()).isEqualTo(UPDATED_THEME);
                assertThat(testEventSearch.getDateStart()).isEqualTo(UPDATED_DATE_START);
                assertThat(testEventSearch.getDateEnd()).isEqualTo(UPDATED_DATE_END);
                assertThat(testEventSearch.getPlace()).isEqualTo(UPDATED_PLACE);
                assertThat(testEventSearch.getPlaceDetails()).isEqualTo(UPDATED_PLACE_DETAILS);
                assertThat(testEventSearch.getAdress()).isEqualTo(UPDATED_ADRESS);
                assertThat(testEventSearch.getNote()).isEqualTo(UPDATED_NOTE);
            });
    }

    @Test
    @Transactional
    void putNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent.description(UPDATED_DESCRIPTION).theme(UPDATED_THEME).place(UPDATED_PLACE).note(UPDATED_NOTE);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getTheme()).isEqualTo(UPDATED_THEME);
        assertThat(testEvent.getDateStart()).isEqualTo(DEFAULT_DATE_START);
        assertThat(testEvent.getDateEnd()).isEqualTo(DEFAULT_DATE_END);
        assertThat(testEvent.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testEvent.getPlaceDetails()).isEqualTo(DEFAULT_PLACE_DETAILS);
        assertThat(testEvent.getAdress()).isEqualTo(DEFAULT_ADRESS);
        assertThat(testEvent.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    @Transactional
    void fullUpdateEventWithPatch() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event using partial update
        Event partialUpdatedEvent = new Event();
        partialUpdatedEvent.setId(event.getId());

        partialUpdatedEvent
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .theme(UPDATED_THEME)
            .dateStart(UPDATED_DATE_START)
            .dateEnd(UPDATED_DATE_END)
            .place(UPDATED_PLACE)
            .placeDetails(UPDATED_PLACE_DETAILS)
            .adress(UPDATED_ADRESS)
            .note(UPDATED_NOTE);

        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEvent))
            )
            .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = eventList.get(eventList.size() - 1);
        assertThat(testEvent.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEvent.getTheme()).isEqualTo(UPDATED_THEME);
        assertThat(testEvent.getDateStart()).isEqualTo(UPDATED_DATE_START);
        assertThat(testEvent.getDateEnd()).isEqualTo(UPDATED_DATE_END);
        assertThat(testEvent.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testEvent.getPlaceDetails()).isEqualTo(UPDATED_PLACE_DETAILS);
        assertThat(testEvent.getAdress()).isEqualTo(UPDATED_ADRESS);
        assertThat(testEvent.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    @Transactional
    void patchNonExistingEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, event.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(event))
            )
            .andExpect(status().isBadRequest());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvent() throws Exception {
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        event.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(event)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Event in the database
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        eventRepository.save(event);
        eventSearchRepository.save(event);

        int databaseSizeBeforeDelete = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the event
        restEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, event.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Event> eventList = eventRepository.findAll();
        assertThat(eventList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEvent() throws Exception {
        // Initialize the database
        event = eventRepository.saveAndFlush(event);
        eventSearchRepository.save(event);

        // Search the event
        restEventMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].theme").value(hasItem(DEFAULT_THEME)))
            .andExpect(jsonPath("$.[*].dateStart").value(hasItem(DEFAULT_DATE_START.toString())))
            .andExpect(jsonPath("$.[*].dateEnd").value(hasItem(DEFAULT_DATE_END.toString())))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE)))
            .andExpect(jsonPath("$.[*].placeDetails").value(hasItem(DEFAULT_PLACE_DETAILS)))
            .andExpect(jsonPath("$.[*].adress").value(hasItem(DEFAULT_ADRESS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }
}
