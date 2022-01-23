package org.contab2.repository;

import org.contab2.domain.Fornitore;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Fornitore entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FornitoreRepository extends JpaRepository<Fornitore, Long> {}
