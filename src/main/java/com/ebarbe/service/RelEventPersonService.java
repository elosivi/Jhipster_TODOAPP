package com.ebarbe.service;

import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.repository.search.RelEventPersonSearchRepository;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.service.mapper.RelEventPersonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.RelEventPerson}.
 */
@Service
@Transactional
public class RelEventPersonService {

    private final Logger log = LoggerFactory.getLogger(RelEventPersonService.class);

    private final RelEventPersonRepository relEventPersonRepository;

    private final RelEventPersonMapper relEventPersonMapper;

    private final RelEventPersonSearchRepository relEventPersonSearchRepository;

    public RelEventPersonService(
        RelEventPersonRepository relEventPersonRepository,
        RelEventPersonMapper relEventPersonMapper,
        RelEventPersonSearchRepository relEventPersonSearchRepository
    ) {
        this.relEventPersonRepository = relEventPersonRepository;
        this.relEventPersonMapper = relEventPersonMapper;
        this.relEventPersonSearchRepository = relEventPersonSearchRepository;
    }

    /**
     * Save a relEventPerson.
     *
     * @param relEventPersonDTO the entity to save.
     * @return the persisted entity.
     */
    public RelEventPersonDTO save(RelEventPersonDTO relEventPersonDTO) {
        log.debug("Request to save RelEventPerson : {}", relEventPersonDTO);
        RelEventPerson relEventPerson = relEventPersonMapper.toEntity(relEventPersonDTO);
        relEventPerson = relEventPersonRepository.save(relEventPerson);
        RelEventPersonDTO result = relEventPersonMapper.toDto(relEventPerson);
        relEventPersonSearchRepository.index(relEventPerson);
        return result;
    }

    /**
     * /!\ do not use beacause relEventPerson doesn't have id  => RelEventPersonExtendedService.update()
     * TO CLEAN
     * Update a relEventPerson.
     *
     * @param relEventPersonDTO the entity to save.
     * @return the persisted entity.
     */
    public RelEventPersonDTO update(RelEventPersonDTO relEventPersonDTO) {
        log.debug("Request to update RelEventPerson : {}", relEventPersonDTO);
        RelEventPerson relEventPerson = relEventPersonMapper.toEntity(relEventPersonDTO);
        relEventPerson = relEventPersonRepository.save(relEventPerson);
        RelEventPersonDTO result = relEventPersonMapper.toDto(relEventPerson);
        relEventPersonSearchRepository.index(relEventPerson);
        return result;
    }

    /**
     * * /!\ do not use beacause relEventPerson doesn't have id  => RelEventPersonExtendedService.update()
     * TO CLEAN
     * Partially update a relEventPerson.
     *
     * @param relEventPersonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RelEventPersonDTO> partialUpdate(RelEventPersonDTO relEventPersonDTO) {
        log.debug("Request to partially update RelEventPerson : {}", relEventPersonDTO);

        return relEventPersonRepository
            .findById(relEventPersonDTO.getId())
            .map(existingRelEventPerson -> {
                relEventPersonMapper.partialUpdate(existingRelEventPerson, relEventPersonDTO);

                return existingRelEventPerson;
            })
            .map(relEventPersonRepository::save)
            .map(savedRelEventPerson -> {
                relEventPersonSearchRepository.index(savedRelEventPerson);
                return savedRelEventPerson;
            })
            .map(relEventPersonMapper::toDto);
    }

    /**
     * Get all the relEventPeople
     * More complete RelEventPersonExtendedRepositoryWithBagRelationships.find...
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RelEventPeople");
        return relEventPersonRepository.findAll(pageable).map(relEventPersonMapper::toDto);
    }

    /**
     * Get all the relEventPeople with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RelEventPersonDTO> findAllWithEagerRelationships(Pageable pageable) {
        return relEventPersonRepository.findAllWithEagerRelationships(pageable).map(relEventPersonMapper::toDto);
    }

    /**
     * /!\ relEventperson doesn't have id, can't use this method => findOneByEventIdAndPersonId
     * Get one relEventPerson by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RelEventPersonDTO> findOne(Long id) {
        log.debug("Request to get RelEventPerson : {}", id);
        return relEventPersonRepository.findOneWithEagerRelationships(id).map(relEventPersonMapper::toDto);
    }

    /**
     * /!\ relEventperson doesn't have id, can't use this method => RelEventPersonExtendedRepository
     * Delete the relEventPerson by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RelEventPerson : {}", id);
        relEventPersonRepository.deleteById(id);
        relEventPersonSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the relEventPerson corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RelEventPersonDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RelEventPeople for query {}", query);
        return relEventPersonSearchRepository.search(query, pageable).map(relEventPersonMapper::toDto);
    }
}
