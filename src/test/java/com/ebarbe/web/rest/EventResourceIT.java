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
import com.ebarbe.domain.Person;
import com.ebarbe.repository.EventRepository;
import com.ebarbe.repository.search.EventSearchRepository;
import com.ebarbe.service.EventService;
import com.ebarbe.service.dto.EventDTO;
import com.ebarbe.service.mapper.EventMapper;
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
    private static final LocalDate SMALLER_DATE_START = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_END = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_END = LocalDate.ofEpochDay(-1L);

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
    private EventMapper eventMapper;

    @Mock
    private EventService eventServiceMock;

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
        EventDTO eventDTO = eventMapper.toDto(event);
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        EventDTO eventDTO = eventMapper.toDto(event);

        int databaseSizeBeforeCreate = eventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eventSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        EventDTO eventDTO = eventMapper.toDto(event);

        restEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
        when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

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
    void getEventsByIdFiltering() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        Long id = event.getId();

        defaultEventShouldBeFound("id.equals=" + id);
        defaultEventShouldNotBeFound("id.notEquals=" + id);

        defaultEventShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.greaterThan=" + id);

        defaultEventShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEventShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where label equals to DEFAULT_LABEL
        defaultEventShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the eventList where label equals to UPDATED_LABEL
        defaultEventShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultEventShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the eventList where label equals to UPDATED_LABEL
        defaultEventShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where label is not null
        defaultEventShouldBeFound("label.specified=true");

        // Get all the eventList where label is null
        defaultEventShouldNotBeFound("label.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByLabelContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where label contains DEFAULT_LABEL
        defaultEventShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the eventList where label contains UPDATED_LABEL
        defaultEventShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where label does not contain DEFAULT_LABEL
        defaultEventShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the eventList where label does not contain UPDATED_LABEL
        defaultEventShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where description equals to DEFAULT_DESCRIPTION
        defaultEventShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the eventList where description equals to UPDATED_DESCRIPTION
        defaultEventShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultEventShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the eventList where description equals to UPDATED_DESCRIPTION
        defaultEventShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where description is not null
        defaultEventShouldBeFound("description.specified=true");

        // Get all the eventList where description is null
        defaultEventShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where description contains DEFAULT_DESCRIPTION
        defaultEventShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the eventList where description contains UPDATED_DESCRIPTION
        defaultEventShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where description does not contain DEFAULT_DESCRIPTION
        defaultEventShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the eventList where description does not contain UPDATED_DESCRIPTION
        defaultEventShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllEventsByThemeIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where theme equals to DEFAULT_THEME
        defaultEventShouldBeFound("theme.equals=" + DEFAULT_THEME);

        // Get all the eventList where theme equals to UPDATED_THEME
        defaultEventShouldNotBeFound("theme.equals=" + UPDATED_THEME);
    }

    @Test
    @Transactional
    void getAllEventsByThemeIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where theme in DEFAULT_THEME or UPDATED_THEME
        defaultEventShouldBeFound("theme.in=" + DEFAULT_THEME + "," + UPDATED_THEME);

        // Get all the eventList where theme equals to UPDATED_THEME
        defaultEventShouldNotBeFound("theme.in=" + UPDATED_THEME);
    }

    @Test
    @Transactional
    void getAllEventsByThemeIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where theme is not null
        defaultEventShouldBeFound("theme.specified=true");

        // Get all the eventList where theme is null
        defaultEventShouldNotBeFound("theme.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByThemeContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where theme contains DEFAULT_THEME
        defaultEventShouldBeFound("theme.contains=" + DEFAULT_THEME);

        // Get all the eventList where theme contains UPDATED_THEME
        defaultEventShouldNotBeFound("theme.contains=" + UPDATED_THEME);
    }

    @Test
    @Transactional
    void getAllEventsByThemeNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where theme does not contain DEFAULT_THEME
        defaultEventShouldNotBeFound("theme.doesNotContain=" + DEFAULT_THEME);

        // Get all the eventList where theme does not contain UPDATED_THEME
        defaultEventShouldBeFound("theme.doesNotContain=" + UPDATED_THEME);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart equals to DEFAULT_DATE_START
        defaultEventShouldBeFound("dateStart.equals=" + DEFAULT_DATE_START);

        // Get all the eventList where dateStart equals to UPDATED_DATE_START
        defaultEventShouldNotBeFound("dateStart.equals=" + UPDATED_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart in DEFAULT_DATE_START or UPDATED_DATE_START
        defaultEventShouldBeFound("dateStart.in=" + DEFAULT_DATE_START + "," + UPDATED_DATE_START);

        // Get all the eventList where dateStart equals to UPDATED_DATE_START
        defaultEventShouldNotBeFound("dateStart.in=" + UPDATED_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart is not null
        defaultEventShouldBeFound("dateStart.specified=true");

        // Get all the eventList where dateStart is null
        defaultEventShouldNotBeFound("dateStart.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart is greater than or equal to DEFAULT_DATE_START
        defaultEventShouldBeFound("dateStart.greaterThanOrEqual=" + DEFAULT_DATE_START);

        // Get all the eventList where dateStart is greater than or equal to UPDATED_DATE_START
        defaultEventShouldNotBeFound("dateStart.greaterThanOrEqual=" + UPDATED_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart is less than or equal to DEFAULT_DATE_START
        defaultEventShouldBeFound("dateStart.lessThanOrEqual=" + DEFAULT_DATE_START);

        // Get all the eventList where dateStart is less than or equal to SMALLER_DATE_START
        defaultEventShouldNotBeFound("dateStart.lessThanOrEqual=" + SMALLER_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart is less than DEFAULT_DATE_START
        defaultEventShouldNotBeFound("dateStart.lessThan=" + DEFAULT_DATE_START);

        // Get all the eventList where dateStart is less than UPDATED_DATE_START
        defaultEventShouldBeFound("dateStart.lessThan=" + UPDATED_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateStartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateStart is greater than DEFAULT_DATE_START
        defaultEventShouldNotBeFound("dateStart.greaterThan=" + DEFAULT_DATE_START);

        // Get all the eventList where dateStart is greater than SMALLER_DATE_START
        defaultEventShouldBeFound("dateStart.greaterThan=" + SMALLER_DATE_START);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd equals to DEFAULT_DATE_END
        defaultEventShouldBeFound("dateEnd.equals=" + DEFAULT_DATE_END);

        // Get all the eventList where dateEnd equals to UPDATED_DATE_END
        defaultEventShouldNotBeFound("dateEnd.equals=" + UPDATED_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd in DEFAULT_DATE_END or UPDATED_DATE_END
        defaultEventShouldBeFound("dateEnd.in=" + DEFAULT_DATE_END + "," + UPDATED_DATE_END);

        // Get all the eventList where dateEnd equals to UPDATED_DATE_END
        defaultEventShouldNotBeFound("dateEnd.in=" + UPDATED_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd is not null
        defaultEventShouldBeFound("dateEnd.specified=true");

        // Get all the eventList where dateEnd is null
        defaultEventShouldNotBeFound("dateEnd.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd is greater than or equal to DEFAULT_DATE_END
        defaultEventShouldBeFound("dateEnd.greaterThanOrEqual=" + DEFAULT_DATE_END);

        // Get all the eventList where dateEnd is greater than or equal to UPDATED_DATE_END
        defaultEventShouldNotBeFound("dateEnd.greaterThanOrEqual=" + UPDATED_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd is less than or equal to DEFAULT_DATE_END
        defaultEventShouldBeFound("dateEnd.lessThanOrEqual=" + DEFAULT_DATE_END);

        // Get all the eventList where dateEnd is less than or equal to SMALLER_DATE_END
        defaultEventShouldNotBeFound("dateEnd.lessThanOrEqual=" + SMALLER_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsLessThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd is less than DEFAULT_DATE_END
        defaultEventShouldNotBeFound("dateEnd.lessThan=" + DEFAULT_DATE_END);

        // Get all the eventList where dateEnd is less than UPDATED_DATE_END
        defaultEventShouldBeFound("dateEnd.lessThan=" + UPDATED_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByDateEndIsGreaterThanSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where dateEnd is greater than DEFAULT_DATE_END
        defaultEventShouldNotBeFound("dateEnd.greaterThan=" + DEFAULT_DATE_END);

        // Get all the eventList where dateEnd is greater than SMALLER_DATE_END
        defaultEventShouldBeFound("dateEnd.greaterThan=" + SMALLER_DATE_END);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where place equals to DEFAULT_PLACE
        defaultEventShouldBeFound("place.equals=" + DEFAULT_PLACE);

        // Get all the eventList where place equals to UPDATED_PLACE
        defaultEventShouldNotBeFound("place.equals=" + UPDATED_PLACE);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where place in DEFAULT_PLACE or UPDATED_PLACE
        defaultEventShouldBeFound("place.in=" + DEFAULT_PLACE + "," + UPDATED_PLACE);

        // Get all the eventList where place equals to UPDATED_PLACE
        defaultEventShouldNotBeFound("place.in=" + UPDATED_PLACE);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where place is not null
        defaultEventShouldBeFound("place.specified=true");

        // Get all the eventList where place is null
        defaultEventShouldNotBeFound("place.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByPlaceContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where place contains DEFAULT_PLACE
        defaultEventShouldBeFound("place.contains=" + DEFAULT_PLACE);

        // Get all the eventList where place contains UPDATED_PLACE
        defaultEventShouldNotBeFound("place.contains=" + UPDATED_PLACE);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where place does not contain DEFAULT_PLACE
        defaultEventShouldNotBeFound("place.doesNotContain=" + DEFAULT_PLACE);

        // Get all the eventList where place does not contain UPDATED_PLACE
        defaultEventShouldBeFound("place.doesNotContain=" + UPDATED_PLACE);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where placeDetails equals to DEFAULT_PLACE_DETAILS
        defaultEventShouldBeFound("placeDetails.equals=" + DEFAULT_PLACE_DETAILS);

        // Get all the eventList where placeDetails equals to UPDATED_PLACE_DETAILS
        defaultEventShouldNotBeFound("placeDetails.equals=" + UPDATED_PLACE_DETAILS);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where placeDetails in DEFAULT_PLACE_DETAILS or UPDATED_PLACE_DETAILS
        defaultEventShouldBeFound("placeDetails.in=" + DEFAULT_PLACE_DETAILS + "," + UPDATED_PLACE_DETAILS);

        // Get all the eventList where placeDetails equals to UPDATED_PLACE_DETAILS
        defaultEventShouldNotBeFound("placeDetails.in=" + UPDATED_PLACE_DETAILS);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where placeDetails is not null
        defaultEventShouldBeFound("placeDetails.specified=true");

        // Get all the eventList where placeDetails is null
        defaultEventShouldNotBeFound("placeDetails.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByPlaceDetailsContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where placeDetails contains DEFAULT_PLACE_DETAILS
        defaultEventShouldBeFound("placeDetails.contains=" + DEFAULT_PLACE_DETAILS);

        // Get all the eventList where placeDetails contains UPDATED_PLACE_DETAILS
        defaultEventShouldNotBeFound("placeDetails.contains=" + UPDATED_PLACE_DETAILS);
    }

    @Test
    @Transactional
    void getAllEventsByPlaceDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where placeDetails does not contain DEFAULT_PLACE_DETAILS
        defaultEventShouldNotBeFound("placeDetails.doesNotContain=" + DEFAULT_PLACE_DETAILS);

        // Get all the eventList where placeDetails does not contain UPDATED_PLACE_DETAILS
        defaultEventShouldBeFound("placeDetails.doesNotContain=" + UPDATED_PLACE_DETAILS);
    }

    @Test
    @Transactional
    void getAllEventsByAdressIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where adress equals to DEFAULT_ADRESS
        defaultEventShouldBeFound("adress.equals=" + DEFAULT_ADRESS);

        // Get all the eventList where adress equals to UPDATED_ADRESS
        defaultEventShouldNotBeFound("adress.equals=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllEventsByAdressIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where adress in DEFAULT_ADRESS or UPDATED_ADRESS
        defaultEventShouldBeFound("adress.in=" + DEFAULT_ADRESS + "," + UPDATED_ADRESS);

        // Get all the eventList where adress equals to UPDATED_ADRESS
        defaultEventShouldNotBeFound("adress.in=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllEventsByAdressIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where adress is not null
        defaultEventShouldBeFound("adress.specified=true");

        // Get all the eventList where adress is null
        defaultEventShouldNotBeFound("adress.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByAdressContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where adress contains DEFAULT_ADRESS
        defaultEventShouldBeFound("adress.contains=" + DEFAULT_ADRESS);

        // Get all the eventList where adress contains UPDATED_ADRESS
        defaultEventShouldNotBeFound("adress.contains=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllEventsByAdressNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where adress does not contain DEFAULT_ADRESS
        defaultEventShouldNotBeFound("adress.doesNotContain=" + DEFAULT_ADRESS);

        // Get all the eventList where adress does not contain UPDATED_ADRESS
        defaultEventShouldBeFound("adress.doesNotContain=" + UPDATED_ADRESS);
    }

    @Test
    @Transactional
    void getAllEventsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where note equals to DEFAULT_NOTE
        defaultEventShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the eventList where note equals to UPDATED_NOTE
        defaultEventShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllEventsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultEventShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the eventList where note equals to UPDATED_NOTE
        defaultEventShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllEventsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where note is not null
        defaultEventShouldBeFound("note.specified=true");

        // Get all the eventList where note is null
        defaultEventShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    void getAllEventsByNoteContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where note contains DEFAULT_NOTE
        defaultEventShouldBeFound("note.contains=" + DEFAULT_NOTE);

        // Get all the eventList where note contains UPDATED_NOTE
        defaultEventShouldNotBeFound("note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllEventsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the eventList where note does not contain DEFAULT_NOTE
        defaultEventShouldNotBeFound("note.doesNotContain=" + DEFAULT_NOTE);

        // Get all the eventList where note does not contain UPDATED_NOTE
        defaultEventShouldBeFound("note.doesNotContain=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllEventsByEventTypeIsEqualToSomething() throws Exception {
        EventType eventType;
        if (TestUtil.findAll(em, EventType.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            eventType = EventTypeResourceIT.createEntity(em);
        } else {
            eventType = TestUtil.findAll(em, EventType.class).get(0);
        }
        em.persist(eventType);
        em.flush();
        event.setEventType(eventType);
        eventRepository.saveAndFlush(event);
        Long eventTypeId = eventType.getId();
        // Get all the eventList where eventType equals to eventTypeId
        defaultEventShouldBeFound("eventTypeId.equals=" + eventTypeId);

        // Get all the eventList where eventType equals to (eventTypeId + 1)
        defaultEventShouldNotBeFound("eventTypeId.equals=" + (eventTypeId + 1));
    }

    @Test
    @Transactional
    void getAllEventsByPersonIsEqualToSomething() throws Exception {
        Person person;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            eventRepository.saveAndFlush(event);
            person = PersonResourceIT.createEntity(em);
        } else {
            person = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(person);
        em.flush();
        event.addPerson(person);
        eventRepository.saveAndFlush(event);
        Long personId = person.getId();
        // Get all the eventList where person equals to personId
        defaultEventShouldBeFound("personId.equals=" + personId);

        // Get all the eventList where person equals to (personId + 1)
        defaultEventShouldNotBeFound("personId.equals=" + (personId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventShouldBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
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

        // Check, that the count call also returns 1
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventShouldNotBeFound(String filter) throws Exception {
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        EventDTO eventDTO = eventMapper.toDto(updatedEvent);

        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(eventDTO))
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

        // Create the Event
        EventDTO eventDTO = eventMapper.toDto(event);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(eventDTO)))
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
