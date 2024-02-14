package com.ebarbe.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelEventPersonExtendedRepository {
    /**
     *  Update a relEventPerson concerned by event and person in param
     * @param eventId
     * @param personId
     * @param newEventId
     * @param newPersonId
     * @param newHierarchyId
     * @param newParticipation
     */
    @Modifying
    @Query(
        "UPDATE RelEventPerson rep " +
        "SET rep.event.id = :newEventId, " +
        "rep.person.id = :newPersonId, " +
        "rep.hierarchy.id = :newHierarchyId, " +
        "rep.participation = :newParticipation " +
        "WHERE rep.event.id = :eventId AND rep.person.id = :personId"
    )
    void updateRelEventPerson(
        @Param("eventId") Long eventId,
        @Param("personId") Long personId,
        @Param("newEventId") Long newEventId,
        @Param("newPersonId") Long newPersonId,
        @Param("newHierarchyId") Long newHierarchyId,
        @Param("newParticipation") String newParticipation
    );
    /*  @Modifying
    @Query("INSERT INTO RelEventPerson (event, person, hierarchy, participation) " +
            "VALUES (:eventId, :personId, :hierarchyId, :participation)"
    )
    void createRelEventPerson(
        @Param("eventId") Long eventId,
        @Param("personId") Long personId,
        @Param("hierarchyId") Long hierarchyId,
        @Param("participation") String participation
    );
*/
    /**
     * remove a relEventPerson concerned by event and person in param
     * @param eventId
     * @param personId
     */
    /* @Modifying
    @Query("DELETE FROM rel_event__person " + "WHERE event_id = :eventId AND person_id = :personId")
    void deleteRelEventPerson(@Param("eventId") Long eventId, @Param("personId") Long personId);
*/
    /**
     * remove all relEventPerson concerned by event in param
     * used when an event is removed
     * @param eventId
     */
    /* @Modifying
    @Query("DELETE FROM rel_event__person " + "WHERE event_id = :eventId")
    void deleteAllByEventId(@Param("eventId") Long eventId);
*/
    /**
     * remove all relEventPerson concerned by person in param
     * used when a person is removed
     * @param personId
     */
    /*  @Modifying
    @Query("DELETE FROM rel_event__person rep " + "WHERE rep.person_id = :personId")
    void deleteAllByPersonId(@Param("personId") Long personId);
*/
    /*@Modifying
    @Query(
        "UPDATE rel_event__person rep " +
            "SET rep.hierarchy_id = :newHierarchyIdOrNull " +
            "WHERE rep.hierarchy_id = :hierarchyIdDeleted "
    )
    void changeRelEventPersonHierarchyIdOrNull( @Param("hierarchyIdDeleted") Long hierarchyIdDeleted, @Param("newHierarchyIdOrNull") Long newHierarchyIdOrNull );
*/
    /**
     * If the modification of an event concern the persons linked: this method will delete several links
     * @param eventId
     * @param personIds
     */
    /* @Modifying
    @Query("DELETE FROM rel_event__person rep WHERE rep.event_id = :eventId AND rep.person_id IN :personIds")
    void deleteByEventIdAndPersonIds(@Param("eventId") Long eventId, @Param("personIds") List<Long> personIds);
*/
}
