package com.ebarbe.web.rest;

import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.service.RelEventPersonQueryService;
import com.ebarbe.service.RelEventPersonService;
import com.ebarbe.service.criteria.RelEventPersonCriteria;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.web.rest.errors.BadRequestAlertException;
import com.ebarbe.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.ebarbe.domain.RelEventPerson}.
 */
@RestController
@RequestMapping("/api/rel-event-people")
public class RelEventPersonResource {

    private final Logger log = LoggerFactory.getLogger(RelEventPersonResource.class);

    private static final String ENTITY_NAME = "relEventPerson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RelEventPersonService relEventPersonService;

    private final RelEventPersonRepository relEventPersonRepository;

    private final RelEventPersonQueryService relEventPersonQueryService;

    public RelEventPersonResource(
        RelEventPersonService relEventPersonService,
        RelEventPersonRepository relEventPersonRepository,
        RelEventPersonQueryService relEventPersonQueryService
    ) {
        this.relEventPersonService = relEventPersonService;
        this.relEventPersonRepository = relEventPersonRepository;
        this.relEventPersonQueryService = relEventPersonQueryService;
    }

    /**
     * {@code POST  /rel-event-people} : Create a new relEventPerson.
     *
     * @param relEventPersonDTO the relEventPersonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relEventPersonDTO, or with status {@code 400 (Bad Request)} if the relEventPerson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RelEventPersonDTO> createRelEventPerson(@RequestBody RelEventPersonDTO relEventPersonDTO)
        throws URISyntaxException {
        log.debug("REST request to save RelEventPerson : {}", relEventPersonDTO);
        if (relEventPersonDTO.getId() != null) {
            throw new BadRequestAlertException("A new relEventPerson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RelEventPersonDTO result = relEventPersonService.save(relEventPersonDTO);
        return ResponseEntity
            .created(new URI("/api/rel-event-people/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rel-event-people} : get all the relEventPeople.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relEventPeople in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RelEventPersonDTO>> getAllRelEventPeople(
        RelEventPersonCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RelEventPeople by criteria: {}", criteria);

        Page<RelEventPersonDTO> page = relEventPersonQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rel-event-people/count} : count all the relEventPeople.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRelEventPeople(RelEventPersonCriteria criteria) {
        log.debug("REST request to count RelEventPeople by criteria: {}", criteria);
        return ResponseEntity.ok().body(relEventPersonQueryService.countByCriteria(criteria));
    }

    /**
     * {@code SEARCH  /rel-event-people/_search?query=:query} : search for the relEventPerson corresponding
     * to the query.
     *
     * @param query the query of the relEventPerson search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<RelEventPersonDTO>> searchRelEventPeople(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of RelEventPeople for query {}", query);
        try {
            Page<RelEventPersonDTO> page = relEventPersonService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
