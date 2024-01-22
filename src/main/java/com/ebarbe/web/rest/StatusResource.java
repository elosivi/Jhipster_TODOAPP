package com.ebarbe.web.rest;

import com.ebarbe.repository.StatusRepository;
import com.ebarbe.service.StatusQueryService;
import com.ebarbe.service.StatusService;
import com.ebarbe.service.criteria.StatusCriteria;
import com.ebarbe.service.dto.StatusDTO;
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
 * REST controller for managing {@link com.ebarbe.domain.Status}.
 */
@RestController
@RequestMapping("/api/statuses")
public class StatusResource {

    private final Logger log = LoggerFactory.getLogger(StatusResource.class);

    private static final String ENTITY_NAME = "status";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusService statusService;

    private final StatusRepository statusRepository;

    private final StatusQueryService statusQueryService;

    public StatusResource(StatusService statusService, StatusRepository statusRepository, StatusQueryService statusQueryService) {
        this.statusService = statusService;
        this.statusRepository = statusRepository;
        this.statusQueryService = statusQueryService;
    }

    /**
     * {@code POST  /statuses} : Create a new status.
     *
     * @param statusDTO the statusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusDTO, or with status {@code 400 (Bad Request)} if the status has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StatusDTO> createStatus(@Valid @RequestBody StatusDTO statusDTO) throws URISyntaxException {
        log.debug("REST request to save Status : {}", statusDTO);
        if (statusDTO.getId() != null) {
            throw new BadRequestAlertException("A new status cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StatusDTO result = statusService.save(statusDTO);
        return ResponseEntity
            .created(new URI("/api/statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /statuses/:id} : Updates an existing status.
     *
     * @param id the id of the statusDTO to save.
     * @param statusDTO the statusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusDTO,
     * or with status {@code 400 (Bad Request)} if the statusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StatusDTO> updateStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StatusDTO statusDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Status : {}, {}", id, statusDTO);
        if (statusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StatusDTO result = statusService.update(statusDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /statuses/:id} : Partial updates given fields of an existing status, field will ignore if it is null
     *
     * @param id the id of the statusDTO to save.
     * @param statusDTO the statusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusDTO,
     * or with status {@code 400 (Bad Request)} if the statusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the statusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StatusDTO> partialUpdateStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StatusDTO statusDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Status partially : {}, {}", id, statusDTO);
        if (statusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StatusDTO> result = statusService.partialUpdate(statusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, statusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /statuses} : get all the statuses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StatusDTO>> getAllStatuses(
        StatusCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Statuses by criteria: {}", criteria);

        Page<StatusDTO> page = statusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /statuses/count} : count all the statuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStatuses(StatusCriteria criteria) {
        log.debug("REST request to count Statuses by criteria: {}", criteria);
        return ResponseEntity.ok().body(statusQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /statuses/:id} : get the "id" status.
     *
     * @param id the id of the statusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StatusDTO> getStatus(@PathVariable("id") Long id) {
        log.debug("REST request to get Status : {}", id);
        Optional<StatusDTO> statusDTO = statusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusDTO);
    }

    /**
     * {@code DELETE  /statuses/:id} : delete the "id" status.
     *
     * @param id the id of the statusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable("id") Long id) {
        log.debug("REST request to delete Status : {}", id);
        statusService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /statuses/_search?query=:query} : search for the status corresponding
     * to the query.
     *
     * @param query the query of the status search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<StatusDTO>> searchStatuses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Statuses for query {}", query);
        try {
            Page<StatusDTO> page = statusService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
