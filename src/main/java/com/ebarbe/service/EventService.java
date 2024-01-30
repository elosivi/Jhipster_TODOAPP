package com.ebarbe.service;

import com.ebarbe.domain.Event;
import com.ebarbe.repository.EventRepository;
import com.ebarbe.repository.search.EventSearchRepository;
import com.ebarbe.service.dto.EventDTO;
import com.ebarbe.service.mapper.EventMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.Event}.
 */
@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final EventSearchRepository eventSearchRepository;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, EventSearchRepository eventSearchRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.eventSearchRepository = eventSearchRepository;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO save(EventDTO eventDTO) {
        log.debug("Request to save Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        EventDTO result = eventMapper.toDto(event);
        eventSearchRepository.index(event);
        return result;
    }

    /**
     * Update a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO update(EventDTO eventDTO) {
        log.debug("Request to update Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        EventDTO result = eventMapper.toDto(event);
        eventSearchRepository.index(event);
        return result;
    }

    /**
     * Partially update a event.
     *
     * @param eventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventDTO> partialUpdate(EventDTO eventDTO) {
        log.debug("Request to partially update Event : {}", eventDTO);

        return eventRepository
            .findById(eventDTO.getId())
            .map(existingEvent -> {
                eventMapper.partialUpdate(existingEvent, eventDTO);

                return existingEvent;
            })
            .map(eventRepository::save)
            .map(savedEvent -> {
                eventSearchRepository.index(savedEvent);
                return savedEvent;
            })
            .map(eventMapper::toDto);
    }

    /**
     * Get all the events.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Events");
        return eventRepository.findAll(pageable).map(eventMapper::toDto);
    }

    /**
     * Get all the events with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eventRepository.findAllWithEagerRelationships(pageable).map(eventMapper::toDto);
    }

    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        log.debug("Request to get Event : {}", id);
        return eventRepository.findOneWithEagerRelationships(id).map(eventMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EventDTO> findOneWithRelation(Long id) {
        log.debug("Request to get Event : {}", id);
        return Optional.ofNullable(eventRepository.findOneWithRelationships(id)).map(eventMapper::toDto);
    }

    private Page<Event> paginateResults(List<Event> resultList, Pageable pageable, long totalElements) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > totalElements ? (int) totalElements : (start + pageable.getPageSize());

        return new PageImpl<>(resultList.subList(start, end), pageable, totalElements);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
        eventSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the event corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Events for query {}", query);
        return eventSearchRepository.search(query, pageable).map(eventMapper::toDto);
    }
}
