package com.ebarbe.web.rest;

import com.ebarbe.domain.MainTask;
import com.ebarbe.repository.MainTaskRepository;
import com.ebarbe.repository.search.MainTaskSearchRepository;
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
 * REST controller for managing {@link com.ebarbe.domain.MainTask}.
 */
@RestController
@RequestMapping("/api/main-tasks")
@Transactional
public class MainTaskResource {

    private final Logger log = LoggerFactory.getLogger(MainTaskResource.class);

    private static final String ENTITY_NAME = "mainTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MainTaskRepository mainTaskRepository;

    private final MainTaskSearchRepository mainTaskSearchRepository;

    public MainTaskResource(MainTaskRepository mainTaskRepository, MainTaskSearchRepository mainTaskSearchRepository) {
        this.mainTaskRepository = mainTaskRepository;
        this.mainTaskSearchRepository = mainTaskSearchRepository;
    }

    /**
     * {@code POST  /main-tasks} : Create a new mainTask.
     *
     * @param mainTask the mainTask to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mainTask, or with status {@code 400 (Bad Request)} if the mainTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MainTask> createMainTask(@Valid @RequestBody MainTask mainTask) throws URISyntaxException {
        log.debug("REST request to save MainTask : {}", mainTask);
        if (mainTask.getId() != null) {
            throw new BadRequestAlertException("A new mainTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MainTask result = mainTaskRepository.save(mainTask);
        mainTaskSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/main-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /main-tasks/:id} : Updates an existing mainTask.
     *
     * @param id the id of the mainTask to save.
     * @param mainTask the mainTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mainTask,
     * or with status {@code 400 (Bad Request)} if the mainTask is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mainTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MainTask> updateMainTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MainTask mainTask
    ) throws URISyntaxException {
        log.debug("REST request to update MainTask : {}, {}", id, mainTask);
        if (mainTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mainTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mainTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MainTask result = mainTaskRepository.save(mainTask);
        mainTaskSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mainTask.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /main-tasks/:id} : Partial updates given fields of an existing mainTask, field will ignore if it is null
     *
     * @param id the id of the mainTask to save.
     * @param mainTask the mainTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mainTask,
     * or with status {@code 400 (Bad Request)} if the mainTask is not valid,
     * or with status {@code 404 (Not Found)} if the mainTask is not found,
     * or with status {@code 500 (Internal Server Error)} if the mainTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MainTask> partialUpdateMainTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MainTask mainTask
    ) throws URISyntaxException {
        log.debug("REST request to partial update MainTask partially : {}, {}", id, mainTask);
        if (mainTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mainTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mainTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MainTask> result = mainTaskRepository
            .findById(mainTask.getId())
            .map(existingMainTask -> {
                if (mainTask.getDescription() != null) {
                    existingMainTask.setDescription(mainTask.getDescription());
                }
                if (mainTask.getDeadline() != null) {
                    existingMainTask.setDeadline(mainTask.getDeadline());
                }
                if (mainTask.getCreation() != null) {
                    existingMainTask.setCreation(mainTask.getCreation());
                }
                if (mainTask.getCost() != null) {
                    existingMainTask.setCost(mainTask.getCost());
                }

                return existingMainTask;
            })
            .map(mainTaskRepository::save)
            .map(savedMainTask -> {
                mainTaskSearchRepository.index(savedMainTask);
                return savedMainTask;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mainTask.getId().toString())
        );
    }

    /**
     * {@code GET  /main-tasks} : get all the mainTasks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mainTasks in body.
     */
    @GetMapping("")
    public List<MainTask> getAllMainTasks() {
        log.debug("REST request to get all MainTasks");
        return mainTaskRepository.findAll();
    }

    /**
     * {@code GET  /main-tasks/:id} : get the "id" mainTask.
     *
     * @param id the id of the mainTask to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mainTask, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MainTask> getMainTask(@PathVariable("id") Long id) {
        log.debug("REST request to get MainTask : {}", id);
        Optional<MainTask> mainTask = mainTaskRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mainTask);
    }

    /**
     * {@code DELETE  /main-tasks/:id} : delete the "id" mainTask.
     *
     * @param id the id of the mainTask to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMainTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete MainTask : {}", id);
        mainTaskRepository.deleteById(id);
        mainTaskSearchRepository.deleteFromIndexById(id);
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
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<MainTask> searchMainTasks(@RequestParam("query") String query) {
        log.debug("REST request to search MainTasks for query {}", query);
        try {
            return StreamSupport.stream(mainTaskSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
