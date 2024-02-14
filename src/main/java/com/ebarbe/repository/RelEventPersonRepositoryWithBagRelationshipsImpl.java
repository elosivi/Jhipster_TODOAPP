package com.ebarbe.repository;

import com.ebarbe.domain.RelEventPerson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * /!\ no method of this class can be used beacause there is no Id in relEventPerson table => RelEventPersonExtendedRepositoryWithBagRelationshipsImpl
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RelEventPersonRepositoryWithBagRelationshipsImpl implements RelEventPersonRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RelEventPerson> fetchBagRelationships(Optional<RelEventPerson> relEventPerson) {
        return relEventPerson.map(this::fetchEvents).map(this::fetchPeople).map(this::fetchHierarchies);
    }

    @Override
    public Page<RelEventPerson> fetchBagRelationships(Page<RelEventPerson> relEventPeople) {
        return new PageImpl<>(
            fetchBagRelationships(relEventPeople.getContent()),
            relEventPeople.getPageable(),
            relEventPeople.getTotalElements()
        );
    }

    @Override
    public List<RelEventPerson> fetchBagRelationships(List<RelEventPerson> relEventPeople) {
        return Optional
            .of(relEventPeople)
            .map(this::fetchEvents)
            .map(this::fetchPeople)
            .map(this::fetchHierarchies)
            .orElse(Collections.emptyList());
    }

    /**
     * /!\ Can't be used beacause there is no Id in relEventPerson table => cf findByEventAndPersonId() / findByEventId() / findByPersonId()
     * get event data for relEventPerson in param
     * @param result
     * @return
     */
    RelEventPerson fetchEvents(RelEventPerson result) {
        return entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.event where relEventPerson.id = :id",
                RelEventPerson.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    /**
     * get event data for each relEventPerson in param
     * uses the RelEventPerson ID to maintain the initial order when sorting the results. Here's where it happens
     * @param relEventPeople
     * @return
     */
    List<RelEventPerson> fetchEvents(List<RelEventPerson> relEventPeople) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, relEventPeople.size()).forEach(index -> order.put(relEventPeople.get(index).getId(), index));
        List<RelEventPerson> result = entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.event where relEventPerson in :relEventPeople",
                RelEventPerson.class
            )
            .setParameter("relEventPeople", relEventPeople)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    /**
     * get person data for relEventPerson in param
     * @param result may be a person object
     * @return
     */
    RelEventPerson fetchPeople(RelEventPerson result) {
        return entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.person where relEventPerson.id = :id",
                RelEventPerson.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    /**
     * get person data for each relEventPerson in param
     * uses the RelEventPerson ID to maintain the initial order when sorting the results. Here's where it happens:
     * @param relEventPeople
     * @return
     */
    List<RelEventPerson> fetchPeople(List<RelEventPerson> relEventPeople) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, relEventPeople.size()).forEach(index -> order.put(relEventPeople.get(index).getId(), index));
        List<RelEventPerson> result = entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.person where relEventPerson in :relEventPeople",
                RelEventPerson.class
            )
            .setParameter("relEventPeople", relEventPeople)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    /**
     * get hierarchy data for the relEventPerson in param
     * @param result may be a hierarchy object
     * @return the RelEventPerson with hierarchy data
     */
    RelEventPerson fetchHierarchies(RelEventPerson result) {
        return entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.hierarchy where relEventPerson.id = :id",
                RelEventPerson.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    /**
     * get hierarchy data for each relEventPerson
     * uses the RelEventPerson ID to maintain the initial order when sorting the results. Here's where it happens:
     * @param relEventPeople
     * @return list of relEventPerson
     */
    List<RelEventPerson> fetchHierarchies(List<RelEventPerson> relEventPeople) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, relEventPeople.size()).forEach(index -> order.put(relEventPeople.get(index).getId(), index));
        List<RelEventPerson> result = entityManager
            .createQuery(
                "select relEventPerson from RelEventPerson relEventPerson left join fetch relEventPerson.hierarchy where relEventPerson in :relEventPeople",
                RelEventPerson.class
            )
            .setParameter("relEventPeople", relEventPeople)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
