package com.ebarbe.web.rest;

import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import com.ebarbe.repository.search.SubTaskSearchRepository;
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
 * REST controller for managing {@link com.ebarbe.domain.SubTask}.
 */
@RestController
@RequestMapping("/api/sub-tasks")
@Transactional
public class SubTaskResource {

    private final Logger log = LoggerFactory.getLogger(SubTaskResource.class);

    private static final String ENTITY_NAME = "subTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubTaskRepository subTaskRepository;

    private final SubTaskSearchRepository subTaskSearchRepository;

    public SubTaskResource(SubTaskRepository subTaskRepository, SubTaskSearchRepository subTaskSearchRepository) {
        this.subTaskRepository = subTaskRepository;
        this.subTaskSearchRepository = subTaskSearchRepository;
    }

    /**
     * {@code POST  /sub-tasks} : Create a new subTask.
     *
     * @param subTask the subTask to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subTask, or with status {@code 400 (Bad Request)} if the subTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubTask> createSubTask(@Valid @RequestBody SubTask subTask) throws URISyntaxException {
        log.debug("REST request to save SubTask : {}", subTask);
        if (subTask.getId() != null) {
            throw new BadRequestAlertException("A new subTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubTask result = subTaskRepository.save(subTask);
        subTaskSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/sub-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sub-tasks/:id} : Updates an existing subTask.
     *
     * @param id the id of the subTask to save.
     * @param subTask the subTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTask,
     * or with status {@code 400 (Bad Request)} if the subTask is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubTask> updateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubTask subTask
    ) throws URISyntaxException {
        log.debug("REST request to update SubTask : {}, {}", id, subTask);
        if (subTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubTask result = subTaskRepository.save(subTask);
        subTaskSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subTask.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sub-tasks/:id} : Partial updates given fields of an existing subTask, field will ignore if it is null
     *
     * @param id the id of the subTask to save.
     * @param subTask the subTask to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subTask,
     * or with status {@code 400 (Bad Request)} if the subTask is not valid,
     * or with status {@code 404 (Not Found)} if the subTask is not found,
     * or with status {@code 500 (Internal Server Error)} if the subTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubTask> partialUpdateSubTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubTask subTask
    ) throws URISyntaxException {
        log.debug("REST request to partial update SubTask partially : {}, {}", id, subTask);
        if (subTask.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subTask.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubTask> result = subTaskRepository
            .findById(subTask.getId())
            .map(existingSubTask -> {
                if (subTask.getDescription() != null) {
                    existingSubTask.setDescription(subTask.getDescription());
                }
                if (subTask.getDeadline() != null) {
                    existingSubTask.setDeadline(subTask.getDeadline());
                }
                if (subTask.getCreation() != null) {
                    existingSubTask.setCreation(subTask.getCreation());
                }
                if (subTask.getCost() != null) {
                    existingSubTask.setCost(subTask.getCost());
                }

                return existingSubTask;
            })
            .map(subTaskRepository::save)
            .map(savedSubTask -> {
                subTaskSearchRepository.index(savedSubTask);
                return savedSubTask;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subTask.getId().toString())
        );
    }

    /**
     * {@code GET  /sub-tasks} : get all the subTasks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subTasks in body.
     */
    @GetMapping("")
    public List<SubTask> getAllSubTasks() {
        log.debug("REST request to get all SubTasks");
        return subTaskRepository.findAll();
    }

    /**
     * {@code GET  /sub-tasks/:id} : get the "id" subTask.
     *
     * @param id the id of the subTask to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subTask, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubTask> getSubTask(@PathVariable("id") Long id) {
        log.debug("REST request to get SubTask : {}", id);
        Optional<SubTask> subTask = subTaskRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(subTask);
    }

    /**
     * {@code DELETE  /sub-tasks/:id} : delete the "id" subTask.
     *
     * @param id the id of the subTask to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete SubTask : {}", id);
        subTaskRepository.deleteById(id);
        subTaskSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sub-tasks/_search?query=:query} : search for the subTask corresponding
     * to the query.
     *
     * @param query the query of the subTask search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<SubTask> searchSubTasks(@RequestParam("query") String query) {
        log.debug("REST request to search SubTasks for query {}", query);
        try {
            return StreamSupport.stream(subTaskSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
