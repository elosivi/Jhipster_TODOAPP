package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.MainTask;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.Status;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import com.ebarbe.repository.search.SubTaskSearchRepository;
import com.ebarbe.service.dto.SubTaskDTO;
import com.ebarbe.service.mapper.SubTaskMapper;
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
    private static final LocalDate SMALLER_DEADLINE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_COST = 1D;
    private static final Double UPDATED_COST = 2D;
    private static final Double SMALLER_COST = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/sub-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sub-tasks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Autowired
    private SubTaskMapper subTaskMapper;

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
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);
        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTaskDTO)))
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
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        int databaseSizeBeforeCreate = subTaskRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subTaskSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTaskDTO)))
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
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTaskDTO)))
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
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        restSubTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTaskDTO)))
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
    void getSubTasksByIdFiltering() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        Long id = subTask.getId();

        defaultSubTaskShouldBeFound("id.equals=" + id);
        defaultSubTaskShouldNotBeFound("id.notEquals=" + id);

        defaultSubTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSubTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultSubTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSubTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSubTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where description equals to DEFAULT_DESCRIPTION
        defaultSubTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the subTaskList where description equals to UPDATED_DESCRIPTION
        defaultSubTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSubTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the subTaskList where description equals to UPDATED_DESCRIPTION
        defaultSubTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where description is not null
        defaultSubTaskShouldBeFound("description.specified=true");

        // Get all the subTaskList where description is null
        defaultSubTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllSubTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where description contains DEFAULT_DESCRIPTION
        defaultSubTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the subTaskList where description contains UPDATED_DESCRIPTION
        defaultSubTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where description does not contain DEFAULT_DESCRIPTION
        defaultSubTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the subTaskList where description does not contain UPDATED_DESCRIPTION
        defaultSubTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline equals to DEFAULT_DEADLINE
        defaultSubTaskShouldBeFound("deadline.equals=" + DEFAULT_DEADLINE);

        // Get all the subTaskList where deadline equals to UPDATED_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.equals=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline in DEFAULT_DEADLINE or UPDATED_DEADLINE
        defaultSubTaskShouldBeFound("deadline.in=" + DEFAULT_DEADLINE + "," + UPDATED_DEADLINE);

        // Get all the subTaskList where deadline equals to UPDATED_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.in=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline is not null
        defaultSubTaskShouldBeFound("deadline.specified=true");

        // Get all the subTaskList where deadline is null
        defaultSubTaskShouldNotBeFound("deadline.specified=false");
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline is greater than or equal to DEFAULT_DEADLINE
        defaultSubTaskShouldBeFound("deadline.greaterThanOrEqual=" + DEFAULT_DEADLINE);

        // Get all the subTaskList where deadline is greater than or equal to UPDATED_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.greaterThanOrEqual=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline is less than or equal to DEFAULT_DEADLINE
        defaultSubTaskShouldBeFound("deadline.lessThanOrEqual=" + DEFAULT_DEADLINE);

        // Get all the subTaskList where deadline is less than or equal to SMALLER_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.lessThanOrEqual=" + SMALLER_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsLessThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline is less than DEFAULT_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.lessThan=" + DEFAULT_DEADLINE);

        // Get all the subTaskList where deadline is less than UPDATED_DEADLINE
        defaultSubTaskShouldBeFound("deadline.lessThan=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByDeadlineIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where deadline is greater than DEFAULT_DEADLINE
        defaultSubTaskShouldNotBeFound("deadline.greaterThan=" + DEFAULT_DEADLINE);

        // Get all the subTaskList where deadline is greater than SMALLER_DEADLINE
        defaultSubTaskShouldBeFound("deadline.greaterThan=" + SMALLER_DEADLINE);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation equals to DEFAULT_CREATION
        defaultSubTaskShouldBeFound("creation.equals=" + DEFAULT_CREATION);

        // Get all the subTaskList where creation equals to UPDATED_CREATION
        defaultSubTaskShouldNotBeFound("creation.equals=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsInShouldWork() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation in DEFAULT_CREATION or UPDATED_CREATION
        defaultSubTaskShouldBeFound("creation.in=" + DEFAULT_CREATION + "," + UPDATED_CREATION);

        // Get all the subTaskList where creation equals to UPDATED_CREATION
        defaultSubTaskShouldNotBeFound("creation.in=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation is not null
        defaultSubTaskShouldBeFound("creation.specified=true");

        // Get all the subTaskList where creation is null
        defaultSubTaskShouldNotBeFound("creation.specified=false");
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation is greater than or equal to DEFAULT_CREATION
        defaultSubTaskShouldBeFound("creation.greaterThanOrEqual=" + DEFAULT_CREATION);

        // Get all the subTaskList where creation is greater than or equal to UPDATED_CREATION
        defaultSubTaskShouldNotBeFound("creation.greaterThanOrEqual=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation is less than or equal to DEFAULT_CREATION
        defaultSubTaskShouldBeFound("creation.lessThanOrEqual=" + DEFAULT_CREATION);

        // Get all the subTaskList where creation is less than or equal to SMALLER_CREATION
        defaultSubTaskShouldNotBeFound("creation.lessThanOrEqual=" + SMALLER_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsLessThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation is less than DEFAULT_CREATION
        defaultSubTaskShouldNotBeFound("creation.lessThan=" + DEFAULT_CREATION);

        // Get all the subTaskList where creation is less than UPDATED_CREATION
        defaultSubTaskShouldBeFound("creation.lessThan=" + UPDATED_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCreationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where creation is greater than DEFAULT_CREATION
        defaultSubTaskShouldNotBeFound("creation.greaterThan=" + DEFAULT_CREATION);

        // Get all the subTaskList where creation is greater than SMALLER_CREATION
        defaultSubTaskShouldBeFound("creation.greaterThan=" + SMALLER_CREATION);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost equals to DEFAULT_COST
        defaultSubTaskShouldBeFound("cost.equals=" + DEFAULT_COST);

        // Get all the subTaskList where cost equals to UPDATED_COST
        defaultSubTaskShouldNotBeFound("cost.equals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsInShouldWork() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost in DEFAULT_COST or UPDATED_COST
        defaultSubTaskShouldBeFound("cost.in=" + DEFAULT_COST + "," + UPDATED_COST);

        // Get all the subTaskList where cost equals to UPDATED_COST
        defaultSubTaskShouldNotBeFound("cost.in=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost is not null
        defaultSubTaskShouldBeFound("cost.specified=true");

        // Get all the subTaskList where cost is null
        defaultSubTaskShouldNotBeFound("cost.specified=false");
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost is greater than or equal to DEFAULT_COST
        defaultSubTaskShouldBeFound("cost.greaterThanOrEqual=" + DEFAULT_COST);

        // Get all the subTaskList where cost is greater than or equal to UPDATED_COST
        defaultSubTaskShouldNotBeFound("cost.greaterThanOrEqual=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost is less than or equal to DEFAULT_COST
        defaultSubTaskShouldBeFound("cost.lessThanOrEqual=" + DEFAULT_COST);

        // Get all the subTaskList where cost is less than or equal to SMALLER_COST
        defaultSubTaskShouldNotBeFound("cost.lessThanOrEqual=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsLessThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost is less than DEFAULT_COST
        defaultSubTaskShouldNotBeFound("cost.lessThan=" + DEFAULT_COST);

        // Get all the subTaskList where cost is less than UPDATED_COST
        defaultSubTaskShouldBeFound("cost.lessThan=" + UPDATED_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        subTaskRepository.saveAndFlush(subTask);

        // Get all the subTaskList where cost is greater than DEFAULT_COST
        defaultSubTaskShouldNotBeFound("cost.greaterThan=" + DEFAULT_COST);

        // Get all the subTaskList where cost is greater than SMALLER_COST
        defaultSubTaskShouldBeFound("cost.greaterThan=" + SMALLER_COST);
    }

    @Test
    @Transactional
    void getAllSubTasksByMainTaskIsEqualToSomething() throws Exception {
        MainTask mainTask;
        if (TestUtil.findAll(em, MainTask.class).isEmpty()) {
            subTaskRepository.saveAndFlush(subTask);
            mainTask = MainTaskResourceIT.createEntity(em);
        } else {
            mainTask = TestUtil.findAll(em, MainTask.class).get(0);
        }
        em.persist(mainTask);
        em.flush();
        subTask.setMainTask(mainTask);
        subTaskRepository.saveAndFlush(subTask);
        Long mainTaskId = mainTask.getId();
        // Get all the subTaskList where mainTask equals to mainTaskId
        defaultSubTaskShouldBeFound("mainTaskId.equals=" + mainTaskId);

        // Get all the subTaskList where mainTask equals to (mainTaskId + 1)
        defaultSubTaskShouldNotBeFound("mainTaskId.equals=" + (mainTaskId + 1));
    }

    @Test
    @Transactional
    void getAllSubTasksByPersonDoerIsEqualToSomething() throws Exception {
        Person personDoer;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            subTaskRepository.saveAndFlush(subTask);
            personDoer = PersonResourceIT.createEntity(em);
        } else {
            personDoer = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(personDoer);
        em.flush();
        subTask.setPersonDoer(personDoer);
        subTaskRepository.saveAndFlush(subTask);
        Long personDoerId = personDoer.getId();
        // Get all the subTaskList where personDoer equals to personDoerId
        defaultSubTaskShouldBeFound("personDoerId.equals=" + personDoerId);

        // Get all the subTaskList where personDoer equals to (personDoerId + 1)
        defaultSubTaskShouldNotBeFound("personDoerId.equals=" + (personDoerId + 1));
    }

    @Test
    @Transactional
    void getAllSubTasksByStatusIsEqualToSomething() throws Exception {
        Status status;
        if (TestUtil.findAll(em, Status.class).isEmpty()) {
            subTaskRepository.saveAndFlush(subTask);
            status = StatusResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, Status.class).get(0);
        }
        em.persist(status);
        em.flush();
        subTask.setStatus(status);
        subTaskRepository.saveAndFlush(subTask);
        Long statusId = status.getId();
        // Get all the subTaskList where status equals to statusId
        defaultSubTaskShouldBeFound("statusId.equals=" + statusId);

        // Get all the subTaskList where status equals to (statusId + 1)
        defaultSubTaskShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubTaskShouldBeFound(String filter) throws Exception {
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())));

        // Check, that the count call also returns 1
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSubTaskShouldNotBeFound(String filter) throws Exception {
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSubTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
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
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(updatedSubTask);

        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subTaskDTO)))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subTaskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
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

        // Create the SubTask
        SubTaskDTO subTaskDTO = subTaskMapper.toDto(subTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubTaskMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(subTaskDTO))
            )
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
