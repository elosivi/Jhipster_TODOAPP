package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.Event;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.repository.search.RelEventPersonSearchRepository;
import com.ebarbe.service.RelEventPersonService;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.service.mapper.RelEventPersonMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link RelEventPersonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RelEventPersonResourceIT {

    private static final String DEFAULT_PARTICIPATION = "AAAAAAAAAA";
    private static final String UPDATED_PARTICIPATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rel-event-people";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/rel-event-people/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RelEventPersonRepository relEventPersonRepository;

    @Mock
    private RelEventPersonRepository relEventPersonRepositoryMock;

    @Autowired
    private RelEventPersonMapper relEventPersonMapper;

    @Mock
    private RelEventPersonService relEventPersonServiceMock;

    @Autowired
    private RelEventPersonSearchRepository relEventPersonSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRelEventPersonMockMvc;

    private RelEventPerson relEventPerson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelEventPerson createEntity(EntityManager em) {
        RelEventPerson relEventPerson = new RelEventPerson().participation(DEFAULT_PARTICIPATION);
        return relEventPerson;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelEventPerson createUpdatedEntity(EntityManager em) {
        RelEventPerson relEventPerson = new RelEventPerson().participation(UPDATED_PARTICIPATION);
        return relEventPerson;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        relEventPersonSearchRepository.deleteAll();
        assertThat(relEventPersonSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        relEventPerson = createEntity(em);
    }

    @Test
    @Transactional
    void createRelEventPerson() throws Exception {
        int databaseSizeBeforeCreate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);
        restRelEventPersonMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RelEventPerson testRelEventPerson = relEventPersonList.get(relEventPersonList.size() - 1);
        assertThat(testRelEventPerson.getParticipation()).isEqualTo(DEFAULT_PARTICIPATION);
    }

    @Test
    @Transactional
    void createRelEventPersonWithExistingId() throws Exception {
        // Create the RelEventPerson with an existing ID
        relEventPerson.setId(1L);
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        int databaseSizeBeforeCreate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRelEventPersonMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRelEventPeople() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(relEventPerson.getId().intValue())))
            .andExpect(jsonPath("$.[*].participation").value(hasItem(DEFAULT_PARTICIPATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRelEventPeopleWithEagerRelationshipsIsEnabled() throws Exception {
        when(relEventPersonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRelEventPersonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(relEventPersonServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRelEventPeopleWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(relEventPersonServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRelEventPersonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(relEventPersonRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRelEventPerson() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get the relEventPerson
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL_ID, relEventPerson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(relEventPerson.getId().intValue()))
            .andExpect(jsonPath("$.participation").value(DEFAULT_PARTICIPATION));
    }

    @Test
    @Transactional
    void getRelEventPeopleByIdFiltering() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        Long id = relEventPerson.getId();

        defaultRelEventPersonShouldBeFound("id.equals=" + id);
        defaultRelEventPersonShouldNotBeFound("id.notEquals=" + id);

        defaultRelEventPersonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRelEventPersonShouldNotBeFound("id.greaterThan=" + id);

        defaultRelEventPersonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRelEventPersonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByParticipationIsEqualToSomething() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList where participation equals to DEFAULT_PARTICIPATION
        defaultRelEventPersonShouldBeFound("participation.equals=" + DEFAULT_PARTICIPATION);

        // Get all the relEventPersonList where participation equals to UPDATED_PARTICIPATION
        defaultRelEventPersonShouldNotBeFound("participation.equals=" + UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByParticipationIsInShouldWork() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList where participation in DEFAULT_PARTICIPATION or UPDATED_PARTICIPATION
        defaultRelEventPersonShouldBeFound("participation.in=" + DEFAULT_PARTICIPATION + "," + UPDATED_PARTICIPATION);

        // Get all the relEventPersonList where participation equals to UPDATED_PARTICIPATION
        defaultRelEventPersonShouldNotBeFound("participation.in=" + UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByParticipationIsNullOrNotNull() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList where participation is not null
        defaultRelEventPersonShouldBeFound("participation.specified=true");

        // Get all the relEventPersonList where participation is null
        defaultRelEventPersonShouldNotBeFound("participation.specified=false");
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByParticipationContainsSomething() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList where participation contains DEFAULT_PARTICIPATION
        defaultRelEventPersonShouldBeFound("participation.contains=" + DEFAULT_PARTICIPATION);

        // Get all the relEventPersonList where participation contains UPDATED_PARTICIPATION
        defaultRelEventPersonShouldNotBeFound("participation.contains=" + UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByParticipationNotContainsSomething() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        // Get all the relEventPersonList where participation does not contain DEFAULT_PARTICIPATION
        defaultRelEventPersonShouldNotBeFound("participation.doesNotContain=" + DEFAULT_PARTICIPATION);

        // Get all the relEventPersonList where participation does not contain UPDATED_PARTICIPATION
        defaultRelEventPersonShouldBeFound("participation.doesNotContain=" + UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            relEventPersonRepository.saveAndFlush(relEventPerson);
            event = EventResourceIT.createEntity(em);
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        relEventPerson.setEvent(event);
        relEventPersonRepository.saveAndFlush(relEventPerson);
        Long eventId = event.getId();
        // Get all the relEventPersonList where event equals to eventId
        defaultRelEventPersonShouldBeFound("eventId.equals=" + eventId);

        // Get all the relEventPersonList where event equals to (eventId + 1)
        defaultRelEventPersonShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByPersonIsEqualToSomething() throws Exception {
        Person person;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            relEventPersonRepository.saveAndFlush(relEventPerson);
            person = PersonResourceIT.createEntity(em);
        } else {
            person = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(person);
        em.flush();
        relEventPerson.setPerson(person);
        relEventPersonRepository.saveAndFlush(relEventPerson);
        Long personId = person.getId();
        // Get all the relEventPersonList where person equals to personId
        defaultRelEventPersonShouldBeFound("personId.equals=" + personId);

        // Get all the relEventPersonList where person equals to (personId + 1)
        defaultRelEventPersonShouldNotBeFound("personId.equals=" + (personId + 1));
    }

    @Test
    @Transactional
    void getAllRelEventPeopleByHierarchyIsEqualToSomething() throws Exception {
        Hierarchy hierarchy;
        if (TestUtil.findAll(em, Hierarchy.class).isEmpty()) {
            relEventPersonRepository.saveAndFlush(relEventPerson);
            hierarchy = HierarchyResourceIT.createEntity(em);
        } else {
            hierarchy = TestUtil.findAll(em, Hierarchy.class).get(0);
        }
        em.persist(hierarchy);
        em.flush();
        relEventPerson.setHierarchy(hierarchy);
        relEventPersonRepository.saveAndFlush(relEventPerson);
        Long hierarchyId = hierarchy.getId();
        // Get all the relEventPersonList where hierarchy equals to hierarchyId
        defaultRelEventPersonShouldBeFound("hierarchyId.equals=" + hierarchyId);

        // Get all the relEventPersonList where hierarchy equals to (hierarchyId + 1)
        defaultRelEventPersonShouldNotBeFound("hierarchyId.equals=" + (hierarchyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRelEventPersonShouldBeFound(String filter) throws Exception {
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(relEventPerson.getId().intValue())))
            .andExpect(jsonPath("$.[*].participation").value(hasItem(DEFAULT_PARTICIPATION)));

        // Check, that the count call also returns 1
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRelEventPersonShouldNotBeFound(String filter) throws Exception {
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRelEventPersonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRelEventPerson() throws Exception {
        // Get the relEventPerson
        restRelEventPersonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRelEventPerson() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        relEventPersonSearchRepository.save(relEventPerson);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());

        // Update the relEventPerson
        RelEventPerson updatedRelEventPerson = relEventPersonRepository.findById(relEventPerson.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRelEventPerson are not directly saved in db
        em.detach(updatedRelEventPerson);
        updatedRelEventPerson.participation(UPDATED_PARTICIPATION);
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(updatedRelEventPerson);

        restRelEventPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relEventPersonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isOk());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        RelEventPerson testRelEventPerson = relEventPersonList.get(relEventPersonList.size() - 1);
        assertThat(testRelEventPerson.getParticipation()).isEqualTo(UPDATED_PARTICIPATION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RelEventPerson> relEventPersonSearchList = IterableUtils.toList(relEventPersonSearchRepository.findAll());
                RelEventPerson testRelEventPersonSearch = relEventPersonSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRelEventPersonSearch.getParticipation()).isEqualTo(UPDATED_PARTICIPATION);
            });
    }

    @Test
    @Transactional
    void putNonExistingRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relEventPersonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRelEventPersonWithPatch() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();

        // Update the relEventPerson using partial update
        RelEventPerson partialUpdatedRelEventPerson = new RelEventPerson();
        partialUpdatedRelEventPerson.setId(relEventPerson.getId());

        partialUpdatedRelEventPerson.participation(UPDATED_PARTICIPATION);

        restRelEventPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelEventPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRelEventPerson))
            )
            .andExpect(status().isOk());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        RelEventPerson testRelEventPerson = relEventPersonList.get(relEventPersonList.size() - 1);
        assertThat(testRelEventPerson.getParticipation()).isEqualTo(UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void fullUpdateRelEventPersonWithPatch() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);

        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();

        // Update the relEventPerson using partial update
        RelEventPerson partialUpdatedRelEventPerson = new RelEventPerson();
        partialUpdatedRelEventPerson.setId(relEventPerson.getId());

        partialUpdatedRelEventPerson.participation(UPDATED_PARTICIPATION);

        restRelEventPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelEventPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRelEventPerson))
            )
            .andExpect(status().isOk());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        RelEventPerson testRelEventPerson = relEventPersonList.get(relEventPersonList.size() - 1);
        assertThat(testRelEventPerson.getParticipation()).isEqualTo(UPDATED_PARTICIPATION);
    }

    @Test
    @Transactional
    void patchNonExistingRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, relEventPersonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRelEventPerson() throws Exception {
        int databaseSizeBeforeUpdate = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        relEventPerson.setId(longCount.incrementAndGet());

        // Create the RelEventPerson
        RelEventPersonDTO relEventPersonDTO = relEventPersonMapper.toDto(relEventPerson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelEventPersonMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(relEventPersonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelEventPerson in the database
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRelEventPerson() throws Exception {
        // Initialize the database
        relEventPersonRepository.saveAndFlush(relEventPerson);
        relEventPersonRepository.save(relEventPerson);
        relEventPersonSearchRepository.save(relEventPerson);

        int databaseSizeBeforeDelete = relEventPersonRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the relEventPerson
        restRelEventPersonMockMvc
            .perform(delete(ENTITY_API_URL_ID, relEventPerson.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RelEventPerson> relEventPersonList = relEventPersonRepository.findAll();
        assertThat(relEventPersonList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(relEventPersonSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRelEventPerson() throws Exception {
        // Initialize the database
        relEventPerson = relEventPersonRepository.saveAndFlush(relEventPerson);
        relEventPersonSearchRepository.save(relEventPerson);

        // Search the relEventPerson
        restRelEventPersonMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + relEventPerson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(relEventPerson.getId().intValue())))
            .andExpect(jsonPath("$.[*].participation").value(hasItem(DEFAULT_PARTICIPATION)));
    }
}
