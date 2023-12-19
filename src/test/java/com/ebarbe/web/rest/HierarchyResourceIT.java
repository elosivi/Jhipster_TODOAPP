package com.ebarbe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ebarbe.IntegrationTest;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import com.ebarbe.repository.search.HierarchySearchRepository;
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
 * Integration tests for the {@link HierarchyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HierarchyResourceIT {

    private static final String DEFAULT_DESCRIPTION = "Xamgd8";
    private static final String UPDATED_DESCRIPTION = "Ip1";

    private static final String ENTITY_API_URL = "/api/hierarchies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/hierarchies/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HierarchyRepository hierarchyRepository;

    @Autowired
    private HierarchySearchRepository hierarchySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHierarchyMockMvc;

    private Hierarchy hierarchy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hierarchy createEntity(EntityManager em) {
        Hierarchy hierarchy = new Hierarchy().description(DEFAULT_DESCRIPTION);
        return hierarchy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hierarchy createUpdatedEntity(EntityManager em) {
        Hierarchy hierarchy = new Hierarchy().description(UPDATED_DESCRIPTION);
        return hierarchy;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        hierarchySearchRepository.deleteAll();
        assertThat(hierarchySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        hierarchy = createEntity(em);
    }

    @Test
    @Transactional
    void createHierarchy() throws Exception {
        int databaseSizeBeforeCreate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        // Create the Hierarchy
        restHierarchyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hierarchy)))
            .andExpect(status().isCreated());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Hierarchy testHierarchy = hierarchyList.get(hierarchyList.size() - 1);
        assertThat(testHierarchy.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createHierarchyWithExistingId() throws Exception {
        // Create the Hierarchy with an existing ID
        hierarchy.setId(1L);

        int databaseSizeBeforeCreate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restHierarchyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hierarchy)))
            .andExpect(status().isBadRequest());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        // set the field null
        hierarchy.setDescription(null);

        // Create the Hierarchy, which fails.

        restHierarchyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hierarchy)))
            .andExpect(status().isBadRequest());

        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllHierarchies() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);

        // Get all the hierarchyList
        restHierarchyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hierarchy.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getHierarchy() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);

        // Get the hierarchy
        restHierarchyMockMvc
            .perform(get(ENTITY_API_URL_ID, hierarchy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hierarchy.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingHierarchy() throws Exception {
        // Get the hierarchy
        restHierarchyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHierarchy() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);

        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        hierarchySearchRepository.save(hierarchy);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());

        // Update the hierarchy
        Hierarchy updatedHierarchy = hierarchyRepository.findById(hierarchy.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHierarchy are not directly saved in db
        em.detach(updatedHierarchy);
        updatedHierarchy.description(UPDATED_DESCRIPTION);

        restHierarchyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHierarchy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHierarchy))
            )
            .andExpect(status().isOk());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        Hierarchy testHierarchy = hierarchyList.get(hierarchyList.size() - 1);
        assertThat(testHierarchy.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Hierarchy> hierarchySearchList = IterableUtils.toList(hierarchySearchRepository.findAll());
                Hierarchy testHierarchySearch = hierarchySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testHierarchySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hierarchy.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hierarchy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hierarchy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hierarchy)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateHierarchyWithPatch() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);

        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();

        // Update the hierarchy using partial update
        Hierarchy partialUpdatedHierarchy = new Hierarchy();
        partialUpdatedHierarchy.setId(hierarchy.getId());

        restHierarchyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHierarchy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHierarchy))
            )
            .andExpect(status().isOk());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        Hierarchy testHierarchy = hierarchyList.get(hierarchyList.size() - 1);
        assertThat(testHierarchy.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateHierarchyWithPatch() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);

        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();

        // Update the hierarchy using partial update
        Hierarchy partialUpdatedHierarchy = new Hierarchy();
        partialUpdatedHierarchy.setId(hierarchy.getId());

        partialUpdatedHierarchy.description(UPDATED_DESCRIPTION);

        restHierarchyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHierarchy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHierarchy))
            )
            .andExpect(status().isOk());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        Hierarchy testHierarchy = hierarchyList.get(hierarchyList.size() - 1);
        assertThat(testHierarchy.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hierarchy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hierarchy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hierarchy))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHierarchy() throws Exception {
        int databaseSizeBeforeUpdate = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        hierarchy.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHierarchyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(hierarchy))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hierarchy in the database
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteHierarchy() throws Exception {
        // Initialize the database
        hierarchyRepository.saveAndFlush(hierarchy);
        hierarchyRepository.save(hierarchy);
        hierarchySearchRepository.save(hierarchy);

        int databaseSizeBeforeDelete = hierarchyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the hierarchy
        restHierarchyMockMvc
            .perform(delete(ENTITY_API_URL_ID, hierarchy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Hierarchy> hierarchyList = hierarchyRepository.findAll();
        assertThat(hierarchyList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(hierarchySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchHierarchy() throws Exception {
        // Initialize the database
        hierarchy = hierarchyRepository.saveAndFlush(hierarchy);
        hierarchySearchRepository.save(hierarchy);

        // Search the hierarchy
        restHierarchyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + hierarchy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hierarchy.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
