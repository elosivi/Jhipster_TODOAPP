package com.ebarbe.service;

import com.ebarbe.domain.EventType;
import com.ebarbe.repository.EventTypeRepository;
import com.ebarbe.repository.search.EventTypeSearchRepository;
import com.ebarbe.service.dto.EventTypeDTO;
import com.ebarbe.service.mapper.EventTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.EventType}.
 */
@Service
@Transactional
public class EventTypeService {

    private final Logger log = LoggerFactory.getLogger(EventTypeService.class);

    private final EventTypeRepository eventTypeRepository;

    private final EventTypeMapper eventTypeMapper;

    private final EventTypeSearchRepository eventTypeSearchRepository;

    public EventTypeService(
        EventTypeRepository eventTypeRepository,
        EventTypeMapper eventTypeMapper,
        EventTypeSearchRepository eventTypeSearchRepository
    ) {
        this.eventTypeRepository = eventTypeRepository;
        this.eventTypeMapper = eventTypeMapper;
        this.eventTypeSearchRepository = eventTypeSearchRepository;
    }

    /**
     * Save a eventType.
     *
     * @param eventTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public EventTypeDTO save(EventTypeDTO eventTypeDTO) {
        log.debug("Request to save EventType : {}", eventTypeDTO);
        EventType eventType = eventTypeMapper.toEntity(eventTypeDTO);
        eventType = eventTypeRepository.save(eventType);
        EventTypeDTO result = eventTypeMapper.toDto(eventType);
        eventTypeSearchRepository.index(eventType);
        return result;
    }

    /**
     * Update a eventType.
     *
     * @param eventTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public EventTypeDTO update(EventTypeDTO eventTypeDTO) {
        log.debug("Request to update EventType : {}", eventTypeDTO);
        EventType eventType = eventTypeMapper.toEntity(eventTypeDTO);
        eventType = eventTypeRepository.save(eventType);
        EventTypeDTO result = eventTypeMapper.toDto(eventType);
        eventTypeSearchRepository.index(eventType);
        return result;
    }

    /**
     * Partially update a eventType.
     *
     * @param eventTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventTypeDTO> partialUpdate(EventTypeDTO eventTypeDTO) {
        log.debug("Request to partially update EventType : {}", eventTypeDTO);

        return eventTypeRepository
            .findById(eventTypeDTO.getId())
            .map(existingEventType -> {
                eventTypeMapper.partialUpdate(existingEventType, eventTypeDTO);

                return existingEventType;
            })
            .map(eventTypeRepository::save)
            .map(savedEventType -> {
                eventTypeSearchRepository.index(savedEventType);
                return savedEventType;
            })
            .map(eventTypeMapper::toDto);
    }

    /**
     * Get all the eventTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EventTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EventTypes");
        return eventTypeRepository.findAll(pageable).map(eventTypeMapper::toDto);
    }

    /**
     * Get one eventType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventTypeDTO> findOne(Long id) {
        log.debug("Request to get EventType : {}", id);
        return eventTypeRepository.findById(id).map(eventTypeMapper::toDto);
    }

    /**
     * Delete the eventType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EventType : {}", id);
        eventTypeRepository.deleteById(id);
        eventTypeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the eventType corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EventTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of EventTypes for query {}", query);
        return eventTypeSearchRepository.search(query, pageable).map(eventTypeMapper::toDto);
    }
}
