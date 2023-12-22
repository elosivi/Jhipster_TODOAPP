package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.Category;
import com.ebarbe.domain.MainTask;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.Status;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.MainTaskRepository;
import com.ebarbe.repository.search.MainTaskSearchRepository;
import com.ebarbe.service.dto.MainTaskDTO;
import com.ebarbe.service.mapper.MainTaskMapper;
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
 * Integration tests for the {@link MainTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MainTaskResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DEADLINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEADLINE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DEADLINE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;
    private static final Double SMALLER_COST = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/main-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/main-tasks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MainTaskRepository mainTaskRepository;

    @Autowired
    private MainTaskMapper mainTaskMapper;

    @Autowired
    private MainTaskSearchRepository mainTaskSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMainTaskMockMvc;

    private MainTask mainTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MainTask createEntity(EntityManager em) {
        MainTask mainTask = new MainTask()
            .description(DEFAULT_DESCRIPTION)
            .deadline(DEFAULT_DEADLINE)
            .creation(DEFAULT_CREATION)
            .cost(DEFAULT_COST);
        return mainTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MainTask createUpdatedEntity(EntityManager em) {
        MainTask mainTask = new MainTask()
            .description(UPDATED_DESCRIPTION)
            .deadline(UPDATED_DEADLINE)
            .creation(UPDATED_CREATION)
            .cost(UPDATED_COST);
        return mainTask;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        mainTaskSearchRepository.deleteAll();
        assertThat(mainTaskSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        mainTask = createEntity(em);
    }

    @Test
    @Transactional
    void createMainTask() throws Exception {
        int databaseSizeBeforeCreate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);
        restMainTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mainTaskDTO)))
            .andExpect(status().isCreated());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        MainTask testMainTask = mainTaskList.get(mainTaskList.size() - 1);
        assertThat(testMainTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMainTask.getDeadline()).isEqualTo(DEFAULT_DEADLINE);
        assertThat(testMainTask.getCreation()).isEqualTo(DEFAULT_CREATION);
        assertThat(testMainTask.getCost()).isEqualTo(DEFAULT_COST);
    }

    @Test
    @Transactional
    void createMainTaskWithExistingId() throws Exception {
        // Create the MainTask with an existing ID
        mainTask.setId(1L);
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        int databaseSizeBeforeCreate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMainTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mainTaskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDeadlineIsRequired() throws Exception {
        int databaseSizeBeforeTest = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        // set the field null
        mainTask.setDeadline(null);

        // Create the MainTask, which fails.
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        restMainTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mainTaskDTO)))
            .andExpect(status().isBadRequest());

        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMainTasks() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mainTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));
    }

    @Test
    @Transactional
    void getMainTask() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get the mainTask
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, mainTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mainTask.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.deadline").value(DEFAULT_DEADLINE.toString()))
            .andExpect(jsonPath("$.creation").value(DEFAULT_CREATION.toString()))
            .andExpect(jsonPath("$.cost").value(DEFAULT_COST.doubleValue()));
    }

    @Test
    @Transactional
    void getMainTasksByIdFiltering() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        Long id = mainTask.getId();

        defaultMainTaskShouldBeFound("id.equals=" + id);
        defaultMainTaskShouldNotBeFound("id.notEquals=" + id);

        defaultMainTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMainTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultMainTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMainTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMainTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where description equals to DEFAULT_DESCRIPTION
        defaultMainTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the mainTaskList where description equals to UPDATED_DESCRIPTION
        defaultMainTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMainTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultMainTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the mainTaskList where description equals to UPDATED_DESCRIPTION
        defaultMainTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMainTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where description is not null
        defaultMainTaskShouldBeFound("description.specified=true");

        // Get all the mainTaskList where description is null
        defaultMainTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllMainTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where description contains DEFAULT_DESCRIPTION
        defaultMainTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the mainTaskList where description contains UPDATED_DESCRIPTION
        defaultMainTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMainTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where description does not contain DEFAULT_DESCRIPTION
        defaultMainTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the mainTaskList where description does not contain UPDATED_DESCRIPTION
        defaultMainTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline equals to DEFAULT_DEADLINE
        defaultMainTaskShouldBeFound("deadline.equals=" + DEFAULT_DEADLINE);

        // Get all the mainTaskList where deadline equals to UPDATED_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.equals=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline in DEFAULT_DEADLINE or UPDATED_DEADLINE
        defaultMainTaskShouldBeFound("deadline.in=" + DEFAULT_DEADLINE + "," + UPDATED_DEADLINE);

        // Get all the mainTaskList where deadline equals to UPDATED_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.in=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline is not null
        defaultMainTaskShouldBeFound("deadline.specified=true");

        // Get all the mainTaskList where deadline is null
        defaultMainTaskShouldNotBeFound("deadline.specified=false");
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline is greater than or equal to DEFAULT_DEADLINE
        defaultMainTaskShouldBeFound("deadline.greaterThanOrEqual=" + DEFAULT_DEADLINE);

        // Get all the mainTaskList where deadline is greater than or equal to UPDATED_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.greaterThanOrEqual=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline is less than or equal to DEFAULT_DEADLINE
        defaultMainTaskShouldBeFound("deadline.lessThanOrEqual=" + DEFAULT_DEADLINE);

        // Get all the mainTaskList where deadline is less than or equal to SMALLER_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.lessThanOrEqual=" + SMALLER_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsLessThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline is less than DEFAULT_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.lessThan=" + DEFAULT_DEADLINE);

        // Get all the mainTaskList where deadline is less than UPDATED_DEADLINE
        defaultMainTaskShouldBeFound("deadline.lessThan=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByDeadlineIsGreaterThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where deadline is greater than DEFAULT_DEADLINE
        defaultMainTaskShouldNotBeFound("deadline.greaterThan=" + DEFAULT_DEADLINE);

        // Get all the mainTaskList where deadline is greater than SMALLER_DEADLINE
        defaultMainTaskShouldBeFound("deadline.greaterThan=" + SMALLER_DEADLINE);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation equals to DEFAULT_CREATION
        defaultMainTaskShouldBeFound("creation.equals=" + DEFAULT_CREATION);

        // Get all the mainTaskList where creation equals to UPDATED_CREATION
        defaultMainTaskShouldNotBeFound("creation.equals=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsInShouldWork() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation in DEFAULT_CREATION or UPDATED_CREATION
        defaultMainTaskShouldBeFound("creation.in=" + DEFAULT_CREATION + "," + UPDATED_CREATION);

        // Get all the mainTaskList where creation equals to UPDATED_CREATION
        defaultMainTaskShouldNotBeFound("creation.in=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation is not null
        defaultMainTaskShouldBeFound("creation.specified=true");

        // Get all the mainTaskList where creation is null
        defaultMainTaskShouldNotBeFound("creation.specified=false");
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation is greater than or equal to DEFAULT_CREATION
        defaultMainTaskShouldBeFound("creation.greaterThanOrEqual=" + DEFAULT_CREATION);

        // Get all the mainTaskList where creation is greater than or equal to UPDATED_CREATION
        defaultMainTaskShouldNotBeFound("creation.greaterThanOrEqual=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation is less than or equal to DEFAULT_CREATION
        defaultMainTaskShouldBeFound("creation.lessThanOrEqual=" + DEFAULT_CREATION);

        // Get all the mainTaskList where creation is less than or equal to SMALLER_CREATION
        defaultMainTaskShouldNotBeFound("creation.lessThanOrEqual=" + SMALLER_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsLessThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation is less than DEFAULT_CREATION
        defaultMainTaskShouldNotBeFound("creation.lessThan=" + DEFAULT_CREATION);

        // Get all the mainTaskList where creation is less than UPDATED_CREATION
        defaultMainTaskShouldBeFound("creation.lessThan=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCreationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where creation is greater than DEFAULT_CREATION
        defaultMainTaskShouldNotBeFound("creation.greaterThan=" + DEFAULT_CREATION);

        // Get all the mainTaskList where creation is greater than SMALLER_CREATION
        defaultMainTaskShouldBeFound("creation.greaterThan=" + SMALLER_CREATION);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost equals to DEFAULT_COST
        defaultMainTaskShouldBeFound("cost.equals=" + DEFAULT_COST);

        // Get all the mainTaskList where cost equals to UPDATED_COST
        defaultMainTaskShouldNotBeFound("cost.equals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsInShouldWork() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost in DEFAULT_COST or UPDATED_COST
        defaultMainTaskShouldBeFound("cost.in=" + DEFAULT_COST + "," + UPDATED_COST);

        // Get all the mainTaskList where cost equals to UPDATED_COST
        defaultMainTaskShouldNotBeFound("cost.in=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost is not null
        defaultMainTaskShouldBeFound("cost.specified=true");

        // Get all the mainTaskList where cost is null
        defaultMainTaskShouldNotBeFound("cost.specified=false");
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost is greater than or equal to DEFAULT_COST
        defaultMainTaskShouldBeFound("cost.greaterThanOrEqual=" + DEFAULT_COST);

        // Get all the mainTaskList where cost is greater than or equal to UPDATED_COST
        defaultMainTaskShouldNotBeFound("cost.greaterThanOrEqual=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost is less than or equal to DEFAULT_COST
        defaultMainTaskShouldBeFound("cost.lessThanOrEqual=" + DEFAULT_COST);

        // Get all the mainTaskList where cost is less than or equal to SMALLER_COST
        defaultMainTaskShouldNotBeFound("cost.lessThanOrEqual=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsLessThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost is less than DEFAULT_COST
        defaultMainTaskShouldNotBeFound("cost.lessThan=" + DEFAULT_COST);

        // Get all the mainTaskList where cost is less than UPDATED_COST
        defaultMainTaskShouldBeFound("cost.lessThan=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        // Get all the mainTaskList where cost is greater than DEFAULT_COST
        defaultMainTaskShouldNotBeFound("cost.greaterThan=" + DEFAULT_COST);

        // Get all the mainTaskList where cost is greater than SMALLER_COST
        defaultMainTaskShouldBeFound("cost.greaterThan=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllMainTasksByCategoryIsEqualToSomething() throws Exception {
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            mainTaskRepository.saveAndFlush(mainTask);
            category = CategoryResourceIT.createEntity(em);
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        em.persist(category);
        em.flush();
        mainTask.setCategory(category);
        mainTaskRepository.saveAndFlush(mainTask);
        Long categoryId = category.getId();
        // Get all the mainTaskList where category equals to categoryId
        defaultMainTaskShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the mainTaskList where category equals to (categoryId + 1)
        defaultMainTaskShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    @Test
    @Transactional
    void getAllMainTasksByPersonOwnerIsEqualToSomething() throws Exception {
        Person personOwner;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            mainTaskRepository.saveAndFlush(mainTask);
            personOwner = PersonResourceIT.createEntity(em);
        } else {
            personOwner = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(personOwner);
        em.flush();
        mainTask.setPersonOwner(personOwner);
        mainTaskRepository.saveAndFlush(mainTask);
        Long personOwnerId = personOwner.getId();
        // Get all the mainTaskList where personOwner equals to personOwnerId
        defaultMainTaskShouldBeFound("personOwnerId.equals=" + personOwnerId);

        // Get all the mainTaskList where personOwner equals to (personOwnerId + 1)
        defaultMainTaskShouldNotBeFound("personOwnerId.equals=" + (personOwnerId + 1));
    }

    @Test
    @Transactional
    void getAllMainTasksByStatusIsEqualToSomething() throws Exception {
        Status status;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            mainTaskRepository.saveAndFlush(mainTask);
            status = StatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(status);
        em.flush();
        mainTask.setStatus(status);
        mainTaskRepository.saveAndFlush(mainTask);
        Long statusId = status.getId();
        // Get all the mainTaskList where status equals to statusId
        defaultMainTaskShouldBeFound("statusId.equals=" + statusId);

        // Get all the mainTaskList where status equals to (statusId + 1)
        defaultMainTaskShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllMainTasksBySubTaskIsEqualToSomething() throws Exception {
        SubTask subTask;
        if (TestUtil.findAll(em, SubTask.class).isEmpty()) {
            mainTaskRepository.saveAndFlush(mainTask);
            subTask = SubTaskResourceIT.createEntity(em);
        } else {
            subTask = TestUtil.findAll(em, SubTask.class).get(0);
        }
        em.persist(subTask);
        em.flush();
        mainTask.addSubTask(subTask);
        mainTaskRepository.saveAndFlush(mainTask);
        Long subTaskId = subTask.getId();
        // Get all the mainTaskList where subTask equals to subTaskId
        defaultMainTaskShouldBeFound("subTaskId.equals=" + subTaskId);

        // Get all the mainTaskList where subTask equals to (subTaskId + 1)
        defaultMainTaskShouldNotBeFound("subTaskId.equals=" + (subTaskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMainTaskShouldBeFound(String filter) throws Exception {
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mainTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));

        // Check, that the count call also returns 1
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMainTaskShouldNotBeFound(String filter) throws Exception {
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMainTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMainTask() throws Exception {
        // Get the mainTask
        restMainTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMainTask() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        mainTaskSearchRepository.save(mainTask);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());

        // Update the mainTask
        MainTask updatedMainTask = mainTaskRepository.findById(mainTask.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMainTask are not directly saved in db
        em.detach(updatedMainTask);
        updatedMainTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION).cost(UPDATED_COST);
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(updatedMainTask);

        restMainTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mainTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isOk());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        MainTask testMainTask = mainTaskList.get(mainTaskList.size() - 1);
        assertThat(testMainTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMainTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testMainTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testMainTask.getCost()).isEqualTo(UPDATED_COST);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MainTask> mainTaskSearchList = IterableUtils.toList(mainTaskSearchRepository.findAll());
                MainTask testMainTaskSearch = mainTaskSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testMainTaskSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testMainTaskSearch.getDeadline()).isEqualTo(UPDATED_DEADLINE);
                assertThat(testMainTaskSearch.getCreation()).isEqualTo(UPDATED_CREATION);
                assertThat(testMainTaskSearch.getCost()).isEqualTo(UPDATED_COST);
            });
    }

    @Test
    @Transactional
    void putNonExistingMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mainTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mainTaskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMainTaskWithPatch() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();

        // Update the mainTask using partial update
        MainTask partialUpdatedMainTask = new MainTask();
        partialUpdatedMainTask.setId(mainTask.getId());

        partialUpdatedMainTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION);

        restMainTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMainTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMainTask))
            )
            .andExpect(status().isOk());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        MainTask testMainTask = mainTaskList.get(mainTaskList.size() - 1);
        assertThat(testMainTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMainTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testMainTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testMainTask.getCost()).isEqualTo(DEFAULT_COST);
    }

    @Test
    @Transactional
    void fullUpdateMainTaskWithPatch() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);

        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();

        // Update the mainTask using partial update
        MainTask partialUpdatedMainTask = new MainTask();
        partialUpdatedMainTask.setId(mainTask.getId());

        partialUpdatedMainTask.description(UPDATED_DESCRIPTION).deadline(UPDATED_DEADLINE).creation(UPDATED_CREATION).cost(UPDATED_COST);

        restMainTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMainTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMainTask))
            )
            .andExpect(status().isOk());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        MainTask testMainTask = mainTaskList.get(mainTaskList.size() - 1);
        assertThat(testMainTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMainTask.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testMainTask.getCreation()).isEqualTo(UPDATED_CREATION);
        assertThat(testMainTask.getCost()).isEqualTo(UPDATED_COST);
    }

    @Test
    @Transactional
    void patchNonExistingMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mainTaskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMainTask() throws Exception {
        int databaseSizeBeforeUpdate = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        mainTask.setId(longCount.incrementAndGet());

        // Create the MainTask
        MainTaskDTO mainTaskDTO = mainTaskMapper.toDto(mainTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMainTaskMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(mainTaskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MainTask in the database
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMainTask() throws Exception {
        // Initialize the database
        mainTaskRepository.saveAndFlush(mainTask);
        mainTaskRepository.save(mainTask);
        mainTaskSearchRepository.save(mainTask);

        int databaseSizeBeforeDelete = mainTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mainTask
        restMainTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, mainTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MainTask> mainTaskList = mainTaskRepository.findAll();
        assertThat(mainTaskList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mainTaskSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMainTask() throws Exception {
        // Initialize the database
        mainTask = mainTaskRepository.saveAndFlush(mainTask);
        mainTaskSearchRepository.save(mainTask);

        // Search the mainTask
        restMainTaskMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + mainTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mainTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));
    }
}
