package org.contab2.repository;

import org.contab2.domain.FatturePassivo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FatturePassivo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FatturePassivoRepository extends JpaRepository<FatturePassivo, Long>, JpaSpecificationExecutor<FatturePassivo> {}
