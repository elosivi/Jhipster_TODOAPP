package com.ebarbe.web.rest;

import com.ebarbe.repository.MainTaskRepository;
import com.ebarbe.service.MainTaskQueryService;
import com.ebarbe.service.MainTaskService;
import com.ebarbe.service.criteria.MainTaskCriteria;
import com.ebarbe.service.dto.MainTaskDTO;
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
 * REST controller for managing {@link com.ebarbe.domain.MainTask}.
 */
@RestController
@RequestMapping("/api/main-tasks")
public class MainTaskResource {

    private final Logger log = LoggerFactory.getLogger(MainTaskResource.class);

    private static final String ENTITY_NAME = "mainTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MainTaskService mainTaskService;

    private final MainTaskRepository mainTaskRepository;

    private final MainTaskQueryService mainTaskQueryService;

    public MainTaskResource(
        MainTaskService mainTaskService,
        MainTaskRepository mainTaskRepository,
        MainTaskQueryService mainTaskQueryService
    ) {
        this.mainTaskService = mainTaskService;
        this.mainTaskRepository = mainTaskRepository;
        this.mainTaskQueryService = mainTaskQueryService;
    }

    /**
     * {@code POST  /main-tasks} : Create a new mainTask.
     *
     * @param mainTaskDTO the mainTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mainTaskDTO, or with status {@code 400 (Bad Request)} if the mainTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MainTaskDTO> createMainTask(@Valid @RequestBody MainTaskDTO mainTaskDTO) throws URISyntaxException {
        log.debug("REST request to save MainTask : {}", mainTaskDTO);
        if (mainTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new mainTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MainTaskDTO result = mainTaskService.save(mainTaskDTO);
        return ResponseEntity
            .created(new URI("/api/main-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /main-tasks/:id} : Updates an existing mainTask.
     *
     * @param id the id of the mainTaskDTO to save.
     * @param mainTaskDTO the mainTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mainTaskDTO,
     * or with status {@code 400 (Bad Request)} if the mainTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mainTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MainTaskDTO> updateMainTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MainTaskDTO mainTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to update MainTask : {}, {}", id, mainTaskDTO);
        if (mainTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mainTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mainTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MainTaskDTO result = mainTaskService.update(mainTaskDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mainTaskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /main-tasks/:id} : Partial updates given fields of an existing mainTask, field will ignore if it is null
     *
     * @param id the id of the mainTaskDTO to save.
     * @param mainTaskDTO the mainTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mainTaskDTO,
     * or with status {@code 400 (Bad Request)} if the mainTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mainTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mainTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MainTaskDTO> partialUpdateMainTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MainTaskDTO mainTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update MainTask partially : {}, {}", id, mainTaskDTO);
        if (mainTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mainTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mainTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MainTaskDTO> result = mainTaskService.partialUpdate(mainTaskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mainTaskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /main-tasks} : get all the mainTasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mainTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MainTaskDTO>> getAllMainTasks(
        MainTaskCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get MainTasks by criteria: {}", criteria);

        Page<MainTaskDTO> page = mainTaskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /main-tasks/count} : count all the mainTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMainTasks(MainTaskCriteria criteria) {
        log.debug("REST request to count MainTasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(mainTaskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /main-tasks/:id} : get the "id" mainTask.
     *
     * @param id the id of the mainTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mainTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MainTaskDTO> getMainTask(@PathVariable("id") Long id) {
        log.debug("REST request to get MainTask : {}", id);
        Optional<MainTaskDTO> mainTaskDTO = mainTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mainTaskDTO);
    }

    /**
     * {@code DELETE  /main-tasks/:id} : delete the "id" mainTask.
     *
     * @param id the id of the mainTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMainTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete MainTask : {}", id);
        mainTaskService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /main-tasks/_search?query=:query} : search for the mainTask corresponding
     * to the query.
     *
     * @param query the query of the mainTask search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<MainTaskDTO>> searchMainTasks(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of MainTasks for query {}", query);
        try {
            Page<MainTaskDTO> page = mainTaskService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
