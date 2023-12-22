package com.ebarbe.service;

import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import com.ebarbe.repository.search.SubTaskSearchRepository;
import com.ebarbe.service.dto.SubTaskDTO;
import com.ebarbe.service.mapper.SubTaskMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.SubTask}.
 */
@Service
@Transactional
public class SubTaskService {

    private final Logger log = LoggerFactory.getLogger(SubTaskService.class);

    private final SubTaskRepository subTaskRepository;

    private final SubTaskMapper subTaskMapper;

    private final SubTaskSearchRepository subTaskSearchRepository;

    public SubTaskService(
        SubTaskRepository subTaskRepository,
        SubTaskMapper subTaskMapper,
        SubTaskSearchRepository subTaskSearchRepository
    ) {
        this.subTaskRepository = subTaskRepository;
        this.subTaskMapper = subTaskMapper;
        this.subTaskSearchRepository = subTaskSearchRepository;
    }

    /**
     * Save a subTask.
     *
     * @param subTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public SubTaskDTO save(SubTaskDTO subTaskDTO) {
        log.debug("Request to save SubTask : {}", subTaskDTO);
        SubTask subTask = subTaskMapper.toEntity(subTaskDTO);
        subTask = subTaskRepository.save(subTask);
        SubTaskDTO result = subTaskMapper.toDto(subTask);
        subTaskSearchRepository.index(subTask);
        return result;
    }

    /**
     * Update a subTask.
     *
     * @param subTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public SubTaskDTO update(SubTaskDTO subTaskDTO) {
        log.debug("Request to update SubTask : {}", subTaskDTO);
        SubTask subTask = subTaskMapper.toEntity(subTaskDTO);
        subTask = subTaskRepository.save(subTask);
        SubTaskDTO result = subTaskMapper.toDto(subTask);
        subTaskSearchRepository.index(subTask);
        return result;
    }

    /**
     * Partially update a subTask.
     *
     * @param subTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SubTaskDTO> partialUpdate(SubTaskDTO subTaskDTO) {
        log.debug("Request to partially update SubTask : {}", subTaskDTO);

        return subTaskRepository
            .findById(subTaskDTO.getId())
            .map(existingSubTask -> {
                subTaskMapper.partialUpdate(existingSubTask, subTaskDTO);

                return existingSubTask;
            })
            .map(subTaskRepository::save)
            .map(savedSubTask -> {
                subTaskSearchRepository.index(savedSubTask);
                return savedSubTask;
            })
            .map(subTaskMapper::toDto);
    }

    /**
     * Get all the subTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SubTasks");
        return subTaskRepository.findAll(pageable).map(subTaskMapper::toDto);
    }

    /**
     * Get one subTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubTaskDTO> findOne(Long id) {
        log.debug("Request to get SubTask : {}", id);
        return subTaskRepository.findById(id).map(subTaskMapper::toDto);
    }

    /**
     * Delete the subTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SubTask : {}", id);
        subTaskRepository.deleteById(id);
        subTaskSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the subTask corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubTaskDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubTasks for query {}", query);
        return subTaskSearchRepository.search(query, pageable).map(subTaskMapper::toDto);
    }
}
