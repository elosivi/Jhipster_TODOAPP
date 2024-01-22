package com.ebarbe.service;

import com.ebarbe.domain.MainTask;
import com.ebarbe.repository.MainTaskRepository;
import com.ebarbe.repository.search.MainTaskSearchRepository;
import com.ebarbe.service.dto.MainTaskDTO;
import com.ebarbe.service.mapper.MainTaskMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.MainTask}.
 */
@Service
@Transactional
public class MainTaskService {

    private final Logger log = LoggerFactory.getLogger(MainTaskService.class);

    private final MainTaskRepository mainTaskRepository;

    private final MainTaskMapper mainTaskMapper;

    private final MainTaskSearchRepository mainTaskSearchRepository;

    public MainTaskService(
        MainTaskRepository mainTaskRepository,
        MainTaskMapper mainTaskMapper,
        MainTaskSearchRepository mainTaskSearchRepository
    ) {
        this.mainTaskRepository = mainTaskRepository;
        this.mainTaskMapper = mainTaskMapper;
        this.mainTaskSearchRepository = mainTaskSearchRepository;
    }

    /**
     * Save a mainTask.
     *
     * @param mainTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public MainTaskDTO save(MainTaskDTO mainTaskDTO) {
        log.debug("Request to save MainTask : {}", mainTaskDTO);
        MainTask mainTask = mainTaskMapper.toEntity(mainTaskDTO);
        mainTask = mainTaskRepository.save(mainTask);
        MainTaskDTO result = mainTaskMapper.toDto(mainTask);
        mainTaskSearchRepository.index(mainTask);
        return result;
    }

    /**
     * Update a mainTask.
     *
     * @param mainTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public MainTaskDTO update(MainTaskDTO mainTaskDTO) {
        log.debug("Request to update MainTask : {}", mainTaskDTO);
        MainTask mainTask = mainTaskMapper.toEntity(mainTaskDTO);
        mainTask = mainTaskRepository.save(mainTask);
        MainTaskDTO result = mainTaskMapper.toDto(mainTask);
        mainTaskSearchRepository.index(mainTask);
        return result;
    }

    /**
     * Partially update a mainTask.
     *
     * @param mainTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MainTaskDTO> partialUpdate(MainTaskDTO mainTaskDTO) {
        log.debug("Request to partially update MainTask : {}", mainTaskDTO);

        return mainTaskRepository
            .findById(mainTaskDTO.getId())
            .map(existingMainTask -> {
                mainTaskMapper.partialUpdate(existingMainTask, mainTaskDTO);

                return existingMainTask;
            })
            .map(mainTaskRepository::save)
            .map(savedMainTask -> {
                mainTaskSearchRepository.index(savedMainTask);
                return savedMainTask;
            })
            .map(mainTaskMapper::toDto);
    }

    /**
     * Get all the mainTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MainTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MainTasks");
        return mainTaskRepository.findAll(pageable).map(mainTaskMapper::toDto);
    }

    /**
     * Get one mainTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MainTaskDTO> findOne(Long id) {
        log.debug("Request to get MainTask : {}", id);
        return mainTaskRepository.findById(id).map(mainTaskMapper::toDto);
    }

    /**
     * Delete the mainTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete MainTask : {}", id);
        mainTaskRepository.deleteById(id);
        mainTaskSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the mainTask corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MainTaskDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MainTasks for query {}", query);
        return mainTaskSearchRepository.search(query, pageable).map(mainTaskMapper::toDto);
    }
}
