package org.contab2.repository;

import org.contab2.domain.Cantiere;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Cantiere entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CantiereRepository extends JpaRepository<Cantiere, Long> {}
