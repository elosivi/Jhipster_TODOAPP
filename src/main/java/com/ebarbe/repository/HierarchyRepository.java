package com.ebarbe.repository;

import com.ebarbe.domain.Hierarchy;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hierarchy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HierarchyRepository extends JpaRepository<Hierarchy, Long>, JpaSpecificationExecutor<Hierarchy> {}
