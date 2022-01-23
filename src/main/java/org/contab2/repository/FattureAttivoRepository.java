package org.contab2.repository;

import org.contab2.domain.FattureAttivo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FattureAttivo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FattureAttivoRepository extends JpaRepository<FattureAttivo, Long>, JpaSpecificationExecutor<FattureAttivo> {}
