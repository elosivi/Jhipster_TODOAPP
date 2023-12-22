package com.ebarbe.service;

import com.ebarbe.domain.Status;
import com.ebarbe.repository.StatusRepository;
import com.ebarbe.repository.search.StatusSearchRepository;
import com.ebarbe.service.dto.StatusDTO;
import com.ebarbe.service.mapper.StatusMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.Status}.
 */
@Service
@Transactional
public class StatusService {

    private final Logger log = LoggerFactory.getLogger(StatusService.class);

    private final StatusRepository statusRepository;

    private final StatusMapper statusMapper;

    private final StatusSearchRepository statusSearchRepository;

    public StatusService(StatusRepository statusRepository, StatusMapper statusMapper, StatusSearchRepository statusSearchRepository) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
        this.statusSearchRepository = statusSearchRepository;
    }

    /**
     * Save a status.
     *
     * @param statusDTO the entity to save.
     * @return the persisted entity.
     */
    public StatusDTO save(StatusDTO statusDTO) {
        log.debug("Request to save Status : {}", statusDTO);
        Status status = statusMapper.toEntity(statusDTO);
        status = statusRepository.save(status);
        StatusDTO result = statusMapper.toDto(status);
        statusSearchRepository.index(status);
        return result;
    }

    /**
     * Update a status.
     *
     * @param statusDTO the entity to save.
     * @return the persisted entity.
     */
    public StatusDTO update(StatusDTO statusDTO) {
        log.debug("Request to update Status : {}", statusDTO);
        Status status = statusMapper.toEntity(statusDTO);
        status = statusRepository.save(status);
        StatusDTO result = statusMapper.toDto(status);
        statusSearchRepository.index(status);
        return result;
    }

    /**
     * Partially update a status.
     *
     * @param statusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StatusDTO> partialUpdate(StatusDTO statusDTO) {
        log.debug("Request to partially update Status : {}", statusDTO);

        return statusRepository
            .findById(statusDTO.getId())
            .map(existingStatus -> {
                statusMapper.partialUpdate(existingStatus, statusDTO);

                return existingStatus;
            })
            .map(statusRepository::save)
            .map(savedStatus -> {
                statusSearchRepository.index(savedStatus);
                return savedStatus;
            })
            .map(statusMapper::toDto);
    }

    /**
     * Get all the statuses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Statuses");
        return statusRepository.findAll(pageable).map(statusMapper::toDto);
    }

    /**
     * Get one status by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StatusDTO> findOne(Long id) {
        log.debug("Request to get Status : {}", id);
        return statusRepository.findById(id).map(statusMapper::toDto);
    }

    /**
     * Delete the status by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Status : {}", id);
        statusRepository.deleteById(id);
        statusSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the status corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Statuses for query {}", query);
        return statusSearchRepository.search(query, pageable).map(statusMapper::toDto);
    }
}
