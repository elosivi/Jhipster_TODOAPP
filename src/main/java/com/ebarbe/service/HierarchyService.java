package com.ebarbe.service;

import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import com.ebarbe.repository.search.HierarchySearchRepository;
import com.ebarbe.service.dto.HierarchyDTO;
import com.ebarbe.service.mapper.HierarchyMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ebarbe.domain.Hierarchy}.
 */
@Service
@Transactional
public class HierarchyService {

    private final Logger log = LoggerFactory.getLogger(HierarchyService.class);

    private final HierarchyRepository hierarchyRepository;

    private final HierarchyMapper hierarchyMapper;

    private final HierarchySearchRepository hierarchySearchRepository;

    public HierarchyService(
        HierarchyRepository hierarchyRepository,
        HierarchyMapper hierarchyMapper,
        HierarchySearchRepository hierarchySearchRepository
    ) {
        this.hierarchyRepository = hierarchyRepository;
        this.hierarchyMapper = hierarchyMapper;
        this.hierarchySearchRepository = hierarchySearchRepository;
    }

    /**
     * Save a hierarchy.
     *
     * @param hierarchyDTO the entity to save.
     * @return the persisted entity.
     */
    public HierarchyDTO save(HierarchyDTO hierarchyDTO) {
        log.debug("Request to save Hierarchy : {}", hierarchyDTO);
        Hierarchy hierarchy = hierarchyMapper.toEntity(hierarchyDTO);
        hierarchy = hierarchyRepository.save(hierarchy);
        HierarchyDTO result = hierarchyMapper.toDto(hierarchy);
        hierarchySearchRepository.index(hierarchy);
        return result;
    }

    /**
     * Update a hierarchy.
     *
     * @param hierarchyDTO the entity to save.
     * @return the persisted entity.
     */
    public HierarchyDTO update(HierarchyDTO hierarchyDTO) {
        log.debug("Request to update Hierarchy : {}", hierarchyDTO);
        Hierarchy hierarchy = hierarchyMapper.toEntity(hierarchyDTO);
        hierarchy = hierarchyRepository.save(hierarchy);
        HierarchyDTO result = hierarchyMapper.toDto(hierarchy);
        hierarchySearchRepository.index(hierarchy);
        return result;
    }

    /**
     * Partially update a hierarchy.
     *
     * @param hierarchyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HierarchyDTO> partialUpdate(HierarchyDTO hierarchyDTO) {
        log.debug("Request to partially update Hierarchy : {}", hierarchyDTO);

        return hierarchyRepository
            .findById(hierarchyDTO.getId())
            .map(existingHierarchy -> {
                hierarchyMapper.partialUpdate(existingHierarchy, hierarchyDTO);

                return existingHierarchy;
            })
            .map(hierarchyRepository::save)
            .map(savedHierarchy -> {
                hierarchySearchRepository.index(savedHierarchy);
                return savedHierarchy;
            })
            .map(hierarchyMapper::toDto);
    }

    /**
     * Get all the hierarchies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HierarchyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Hierarchies");
        return hierarchyRepository.findAll(pageable).map(hierarchyMapper::toDto);
    }

    /**
     *  Get all the hierarchies where Person is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<HierarchyDTO> findAllWherePersonIsNull() {
        log.debug("Request to get all hierarchies where Person is null");
        return StreamSupport
            .stream(hierarchyRepository.findAll().spliterator(), false)
            .filter(hierarchy -> hierarchy.getPersons() == null)
            .map(hierarchyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one hierarchy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HierarchyDTO> findOne(Long id) {
        log.debug("Request to get Hierarchy : {}", id);
        return hierarchyRepository.findById(id).map(hierarchyMapper::toDto);
    }

    /**
     * Delete the hierarchy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Hierarchy : {}", id);
        hierarchyRepository.deleteById(id);
        hierarchySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the hierarchy corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HierarchyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Hierarchies for query {}", query);
        return hierarchySearchRepository.search(query, pageable).map(hierarchyMapper::toDto);
    }
}
