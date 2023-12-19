package com.ebarbe.web.rest;

import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import com.ebarbe.repository.search.HierarchySearchRepository;
import com.ebarbe.web.rest.errors.BadRequestAlertException;
import com.ebarbe.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ebarbe.domain.Hierarchy}.
 */
@RestController
@RequestMapping("/api/hierarchies")
@Transactional
public class HierarchyResource {

    private final Logger log = LoggerFactory.getLogger(HierarchyResource.class);

    private static final String ENTITY_NAME = "hierarchy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HierarchyRepository hierarchyRepository;

    private final HierarchySearchRepository hierarchySearchRepository;

    public HierarchyResource(HierarchyRepository hierarchyRepository, HierarchySearchRepository hierarchySearchRepository) {
        this.hierarchyRepository = hierarchyRepository;
        this.hierarchySearchRepository = hierarchySearchRepository;
    }

    /**
     * {@code POST  /hierarchies} : Create a new hierarchy.
     *
     * @param hierarchy the hierarchy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hierarchy, or with status {@code 400 (Bad Request)} if the hierarchy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Hierarchy> createHierarchy(@Valid @RequestBody Hierarchy hierarchy) throws URISyntaxException {
        log.debug("REST request to save Hierarchy : {}", hierarchy);
        if (hierarchy.getId() != null) {
            throw new BadRequestAlertException("A new hierarchy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Hierarchy result = hierarchyRepository.save(hierarchy);
        hierarchySearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/hierarchies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /hierarchies/:id} : Updates an existing hierarchy.
     *
     * @param id the id of the hierarchy to save.
     * @param hierarchy the hierarchy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hierarchy,
     * or with status {@code 400 (Bad Request)} if the hierarchy is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hierarchy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Hierarchy> updateHierarchy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Hierarchy hierarchy
    ) throws URISyntaxException {
        log.debug("REST request to update Hierarchy : {}, {}", id, hierarchy);
        if (hierarchy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hierarchy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hierarchyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Hierarchy result = hierarchyRepository.save(hierarchy);
        hierarchySearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hierarchy.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /hierarchies/:id} : Partial updates given fields of an existing hierarchy, field will ignore if it is null
     *
     * @param id the id of the hierarchy to save.
     * @param hierarchy the hierarchy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hierarchy,
     * or with status {@code 400 (Bad Request)} if the hierarchy is not valid,
     * or with status {@code 404 (Not Found)} if the hierarchy is not found,
     * or with status {@code 500 (Internal Server Error)} if the hierarchy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Hierarchy> partialUpdateHierarchy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Hierarchy hierarchy
    ) throws URISyntaxException {
        log.debug("REST request to partial update Hierarchy partially : {}, {}", id, hierarchy);
        if (hierarchy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hierarchy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hierarchyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Hierarchy> result = hierarchyRepository
            .findById(hierarchy.getId())
            .map(existingHierarchy -> {
                if (hierarchy.getDescription() != null) {
                    existingHierarchy.setDescription(hierarchy.getDescription());
                }

                return existingHierarchy;
            })
            .map(hierarchyRepository::save)
            .map(savedHierarchy -> {
                hierarchySearchRepository.index(savedHierarchy);
                return savedHierarchy;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hierarchy.getId().toString())
        );
    }

    /**
     * {@code GET  /hierarchies} : get all the hierarchies.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hierarchies in body.
     */
    @GetMapping("")
    public List<Hierarchy> getAllHierarchies(@RequestParam(name = "filter", required = false) String filter) {
        if ("person-is-null".equals(filter)) {
            log.debug("REST request to get all Hierarchys where person is null");
            return StreamSupport
                .stream(hierarchyRepository.findAll().spliterator(), false)
                .filter(hierarchy -> hierarchy.getPerson() == null)
                .toList();
        }
        log.debug("REST request to get all Hierarchies");
        return hierarchyRepository.findAll();
    }

    /**
     * {@code GET  /hierarchies/:id} : get the "id" hierarchy.
     *
     * @param id the id of the hierarchy to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hierarchy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Hierarchy> getHierarchy(@PathVariable("id") Long id) {
        log.debug("REST request to get Hierarchy : {}", id);
        Optional<Hierarchy> hierarchy = hierarchyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hierarchy);
    }

    /**
     * {@code DELETE  /hierarchies/:id} : delete the "id" hierarchy.
     *
     * @param id the id of the hierarchy to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHierarchy(@PathVariable("id") Long id) {
        log.debug("REST request to delete Hierarchy : {}", id);
        hierarchyRepository.deleteById(id);
        hierarchySearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /hierarchies/_search?query=:query} : search for the hierarchy corresponding
     * to the query.
     *
     * @param query the query of the hierarchy search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Hierarchy> searchHierarchies(@RequestParam("query") String query) {
        log.debug("REST request to search Hierarchies for query {}", query);
        try {
            return StreamSupport.stream(hierarchySearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
