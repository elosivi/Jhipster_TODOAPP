package com.ebarbe.repository;

import com.ebarbe.domain.RelEventPerson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RelEventPersonRepositoryWithBagRelationships {
    Optional<RelEventPerson> fetchBagRelationships(Optional<RelEventPerson> relEventPerson);

    List<RelEventPerson> fetchBagRelationships(List<RelEventPerson> relEventPeople);

    Page<RelEventPerson> fetchBagRelationships(Page<RelEventPerson> relEventPeople);
}
