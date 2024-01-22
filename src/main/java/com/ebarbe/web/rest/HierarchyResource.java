package com.ebarbe.web.rest;

import com.ebarbe.repository.HierarchyRepository;
import com.ebarbe.service.HierarchyQueryService;
import com.ebarbe.service.HierarchyService;
import com.ebarbe.service.criteria.HierarchyCriteria;
import com.ebarbe.service.dto.HierarchyDTO;
import com.ebarbe.web.rest.errors.BadRequestAlertException;
import com.ebarbe.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ebarbe.domain.Hierarchy}.
 */
@RestController
@RequestMapping("/api/hierarchies")
public class HierarchyResource {

    private final Logger log = LoggerFactory.getLogger(HierarchyResource.class);

    private static final String ENTITY_NAME = "hierarchy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HierarchyService hierarchyService;

    private final HierarchyRepository hierarchyRepository;

    private final HierarchyQueryService hierarchyQueryService;

    public HierarchyResource(
        HierarchyService hierarchyService,
        HierarchyRepository hierarchyRepository,
        HierarchyQueryService hierarchyQueryService
    ) {
        this.hierarchyService = hierarchyService;
        this.hierarchyRepository = hierarchyRepository;
        this.hierarchyQueryService = hierarchyQueryService;
    }

    /**
     * {@code POST  /hierarchies} : Create a new hierarchy.
     *
     * @param hierarchyDTO the hierarchyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hierarchyDTO, or with status {@code 400 (Bad Request)} if the hierarchy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HierarchyDTO> createHierarchy(@Valid @RequestBody HierarchyDTO hierarchyDTO) throws URISyntaxException {
        log.debug("REST request to save Hierarchy : {}", hierarchyDTO);
        if (hierarchyDTO.getId() != null) {
            throw new BadRequestAlertException("A new hierarchy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HierarchyDTO result = hierarchyService.save(hierarchyDTO);
        return ResponseEntity
            .created(new URI("/api/hierarchies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /hierarchies/:id} : Updates an existing hierarchy.
     *
     * @param id the id of the hierarchyDTO to save.
     * @param hierarchyDTO the hierarchyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hierarchyDTO,
     * or with status {@code 400 (Bad Request)} if the hierarchyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hierarchyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HierarchyDTO> updateHierarchy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HierarchyDTO hierarchyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Hierarchy : {}, {}", id, hierarchyDTO);
        if (hierarchyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hierarchyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hierarchyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HierarchyDTO result = hierarchyService.update(hierarchyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hierarchyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /hierarchies/:id} : Partial updates given fields of an existing hierarchy, field will ignore if it is null
     *
     * @param id the id of the hierarchyDTO to save.
     * @param hierarchyDTO the hierarchyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hierarchyDTO,
     * or with status {@code 400 (Bad Request)} if the hierarchyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the hierarchyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the hierarchyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HierarchyDTO> partialUpdateHierarchy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HierarchyDTO hierarchyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Hierarchy partially : {}, {}", id, hierarchyDTO);
        if (hierarchyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hierarchyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hierarchyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HierarchyDTO> result = hierarchyService.partialUpdate(hierarchyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hierarchyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /hierarchies} : get all the hierarchies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hierarchies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HierarchyDTO>> getAllHierarchies(
        HierarchyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Hierarchies by criteria: {}", criteria);

        Page<HierarchyDTO> page = hierarchyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /hierarchies/count} : count all the hierarchies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHierarchies(HierarchyCriteria criteria) {
        log.debug("REST request to count Hierarchies by criteria: {}", criteria);
        return ResponseEntity.ok().body(hierarchyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /hierarchies/:id} : get the "id" hierarchy.
     *
     * @param id the id of the hierarchyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hierarchyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HierarchyDTO> getHierarchy(@PathVariable("id") Long id) {
        log.debug("REST request to get Hierarchy : {}", id);
        Optional<HierarchyDTO> hierarchyDTO = hierarchyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(hierarchyDTO);
    }

    /**
     * {@code DELETE  /hierarchies/:id} : delete the "id" hierarchy.
     *
     * @param id the id of the hierarchyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHierarchy(@PathVariable("id") Long id) {
        log.debug("REST request to delete Hierarchy : {}", id);
        hierarchyService.delete(id);
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
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<HierarchyDTO>> searchHierarchies(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Hierarchies for query {}", query);
        try {
            Page<HierarchyDTO> page = hierarchyService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
