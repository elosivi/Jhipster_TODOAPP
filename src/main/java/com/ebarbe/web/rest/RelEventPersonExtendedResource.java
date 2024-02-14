package com.ebarbe.web.rest;

import com.ebarbe.repository.RelEventPersonRepository;
import com.ebarbe.service.RelEventPersonExtendedService;
import com.ebarbe.service.RelEventPersonQueryService;
import com.ebarbe.service.RelEventPersonService;
import com.ebarbe.service.criteria.RelEventPersonCriteria;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.RelEventPersonDTO;
import com.ebarbe.web.rest.errors.BadRequestAlertException;
import com.ebarbe.web.rest.errors.ElasticsearchExceptionMapper;
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
@RequestMapping("/api/rel-event-people/management")
public class RelEventPersonExtendedResource extends RelEventPersonResource {

    private final Logger log = LoggerFactory.getLogger(RelEventPersonExtendedResource.class);

    private static final String ENTITY_NAME = "relEventPerson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RelEventPersonService relEventPersonService;
    private final RelEventPersonExtendedService relEventPersonExtendedService;
    private final RelEventPersonRepository relEventPersonRepository;
    private final RelEventPersonQueryService relEventPersonQueryService;

    public RelEventPersonExtendedResource(
        RelEventPersonService relEventPersonService,
        RelEventPersonExtendedService relEventPersonExtendedService,
        RelEventPersonRepository relEventPersonRepository,
        RelEventPersonQueryService relEventPersonQueryService
    ) {
        super(relEventPersonService, relEventPersonRepository, relEventPersonQueryService);
        this.relEventPersonService = relEventPersonService;
        this.relEventPersonExtendedService = relEventPersonExtendedService;
        this.relEventPersonRepository = relEventPersonRepository;
        this.relEventPersonQueryService = relEventPersonQueryService;
    }

    /*************************************************************************** */
    /*     U P D A T E    O N E     O R    L I S T     */
    /*************************************************************************** */

    /**
     * {@code PUT  /rel-event-people/management/{eventId}/{personId}} : Updates an existing relEventPerson.
     *
     * @param eventId
     * @param personId
     * @param relEventPersonDTONew
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relEventPersonDTO,
     * or with status {@code 400 (Bad Request)} if the relEventPersonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the relEventPersonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @PutMapping("/{eventId}/{personId}")
    public ResponseEntity<RelEventPersonDTO> updateRelEventPerson(
        @PathVariable(value = "eventId", required = true) Long eventId,
        @PathVariable(value = "personId", required = true) Long personId,
        @RequestBody RelEventPersonDTO relEventPersonDTONew
    ) throws URISyntaxException {
        log.debug(
            "REST request to update RelEventPerson concerned by event: {}, and person : {} | new data : {}",
            eventId,
            personId,
            relEventPersonDTONew
        );
        if (relEventPersonDTONew == null) {
            throw new BadRequestAlertException("Invalid new relEventperson", ENTITY_NAME, "is null");
        }
        if (
            !Objects.equals(eventId, relEventPersonDTONew.getEvent().getId()) &&
            !Objects.equals(personId, relEventPersonDTONew.getPerson().getId())
        ) {
            throw new BadRequestAlertException(
                "Event id and Person Id are both different with the itial relEventperson to update",
                ENTITY_NAME,
                "ids invalid"
            );
        }
        boolean isRepToChangeExist = relEventPersonExtendedService.findREPCompleteByEventIdAndPersonId(eventId, personId).isPresent();
        if (!isRepToChangeExist) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "couple eventId and personId not found");
        }
        RelEventPersonDTO result = relEventPersonExtendedService.update(eventId, personId, relEventPersonDTONew);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relEventPersonDTONew.toString()))
            .body(result);
    }

    /*************************************************************************** */
    /*     G E T   O N E   O R    L I S T  */
    /*************************************************************************** */

    /**
     * {@code GET  /rel-event-people/management} : get all the relEventPeople.
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

        Page<RelEventPersonDTO> page = relEventPersonExtendedService.findAllREPComplete(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rel-event-people/management/byEvent/eventId} : get all the relEventPeople by event.
     * GET ALL CONCERNED BY THE EVENT IN PARAM
     * @param eventId
     * @param pageable
     * @return
     */
    @GetMapping("/byEvent/{eventId}")
    public ResponseEntity<List<RelEventPersonDTO>> getAllRelEventPeopleByEvent(
        @PathVariable("eventId") Long eventId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RelEventPeople by event: {}", eventId);

        Page<RelEventPersonDTO> page = relEventPersonExtendedService.findAllREPCompleteByEventId(pageable, eventId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rel-event-people/management/byPerson/personId} : get all the relEventPeople by person.
     * load all events and details concerned by the person Id sended
     * @param personId
     * @param pageable
     * @return
     */
    @GetMapping("/byPerson/{personId}")
    public ResponseEntity<List<RelEventPersonDTO>> getAllRelEventPeopleByPerson(
        @PathVariable("personId") Long personId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RelEventPeople by event: {}", personId);

        Page<RelEventPersonDTO> page = relEventPersonExtendedService.findAllREPCompleteByPersonId(pageable, personId);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelEventPersonDTO> getOneRelEventPeopleById(
        @PathVariable("id") Long id,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RelEventPeople by id: {}", id);
        Optional<RelEventPersonDTO> RelEventPersonDTO = relEventPersonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(RelEventPersonDTO);
    }

    /*************************************************************************** */
    /*     D E L E T E */
    /*************************************************************************** */

    /**
     * {@code DELETE  /rel-event-people/:id} : delete the "id" relEventPerson.
     *
     * @param id the id of the relEventPersonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelEventPerson(@PathVariable("id") Long id) {
        log.debug("REST request to delete RelEventPerson : {}", id);
        relEventPersonService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /*************************************************************************** */
    /*      s e a r c h      */
    /*************************************************************************** */

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
