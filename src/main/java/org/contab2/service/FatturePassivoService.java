package org.contab2.service;

import java.util.List;
import java.util.Optional;
import org.contab2.domain.FatturePassivo;
import org.contab2.repository.FatturePassivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FatturePassivo}.
 */
@Service
@Transactional
public class FatturePassivoService {

    private final Logger log = LoggerFactory.getLogger(FatturePassivoService.class);

    private final FatturePassivoRepository fatturePassivoRepository;

    public FatturePassivoService(FatturePassivoRepository fatturePassivoRepository) {
        this.fatturePassivoRepository = fatturePassivoRepository;
    }

    /**
     * Save a fatturePassivo.
     *
     * @param fatturePassivo the entity to save.
     * @return the persisted entity.
     */
    public FatturePassivo save(FatturePassivo fatturePassivo) {
        log.debug("Request to save FatturePassivo : {}", fatturePassivo);
        return fatturePassivoRepository.save(fatturePassivo);
    }

    /**
     * Partially update a fatturePassivo.
     *
     * @param fatturePassivo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FatturePassivo> partialUpdate(FatturePassivo fatturePassivo) {
        log.debug("Request to partially update FatturePassivo : {}", fatturePassivo);

        return fatturePassivoRepository
            .findById(fatturePassivo.getId())
            .map(existingFatturePassivo -> {
                if (fatturePassivo.getNumeroFattura() != null) {
                    existingFatturePassivo.setNumeroFattura(fatturePassivo.getNumeroFattura());
                }
                if (fatturePassivo.getRagSociale() != null) {
                    existingFatturePassivo.setRagSociale(fatturePassivo.getRagSociale());
                }
                if (fatturePassivo.getImponibile() != null) {
                    existingFatturePassivo.setImponibile(fatturePassivo.getImponibile());
                }
                if (fatturePassivo.getIva() != null) {
                    existingFatturePassivo.setIva(fatturePassivo.getIva());
                }
                if (fatturePassivo.getStato() != null) {
                    existingFatturePassivo.setStato(fatturePassivo.getStato());
                }
                if (fatturePassivo.getDataEmissione() != null) {
                    existingFatturePassivo.setDataEmissione(fatturePassivo.getDataEmissione());
                }
                if (fatturePassivo.getDataPagamento() != null) {
                    existingFatturePassivo.setDataPagamento(fatturePassivo.getDataPagamento());
                }

                return existingFatturePassivo;
            })
            .map(fatturePassivoRepository::save);
    }

    /**
     * Get all the fatturePassivos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FatturePassivo> findAll() {
        log.debug("Request to get all FatturePassivos");
        return fatturePassivoRepository.findAll();
    }

    /**
     * Get one fatturePassivo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FatturePassivo> findOne(Long id) {
        log.debug("Request to get FatturePassivo : {}", id);
        return fatturePassivoRepository.findById(id);
    }

    /**
     * Delete the fatturePassivo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FatturePassivo : {}", id);
        fatturePassivoRepository.deleteById(id);
    }
}
