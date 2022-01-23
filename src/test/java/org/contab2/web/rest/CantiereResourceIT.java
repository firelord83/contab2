package org.contab2.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.contab2.IntegrationTest;
import org.contab2.domain.Cantiere;
import org.contab2.repository.CantiereRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CantiereResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CantiereResourceIT {

    private static final String DEFAULT_NOME_CANTIERE = "AAAAAAAAAA";
    private static final String UPDATED_NOME_CANTIERE = "BBBBBBBBBB";

    private static final String DEFAULT_INDIRIZZO = "AAAAAAAAAA";
    private static final String UPDATED_INDIRIZZO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cantieres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CantiereRepository cantiereRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCantiereMockMvc;

    private Cantiere cantiere;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cantiere createEntity(EntityManager em) {
        Cantiere cantiere = new Cantiere().nomeCantiere(DEFAULT_NOME_CANTIERE).indirizzo(DEFAULT_INDIRIZZO);
        return cantiere;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cantiere createUpdatedEntity(EntityManager em) {
        Cantiere cantiere = new Cantiere().nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);
        return cantiere;
    }

    @BeforeEach
    public void initTest() {
        cantiere = createEntity(em);
    }

    @Test
    @Transactional
    void createCantiere() throws Exception {
        int databaseSizeBeforeCreate = cantiereRepository.findAll().size();
        // Create the Cantiere
        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isCreated());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeCreate + 1);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(DEFAULT_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(DEFAULT_INDIRIZZO);
    }

    @Test
    @Transactional
    void createCantiereWithExistingId() throws Exception {
        // Create the Cantiere with an existing ID
        cantiere.setId(1L);

        int databaseSizeBeforeCreate = cantiereRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeCantiereIsRequired() throws Exception {
        int databaseSizeBeforeTest = cantiereRepository.findAll().size();
        // set the field null
        cantiere.setNomeCantiere(null);

        // Create the Cantiere, which fails.

        restCantiereMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isBadRequest());

        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCantieres() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        // Get all the cantiereList
        restCantiereMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cantiere.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomeCantiere").value(hasItem(DEFAULT_NOME_CANTIERE)))
            .andExpect(jsonPath("$.[*].indirizzo").value(hasItem(DEFAULT_INDIRIZZO)));
    }

    @Test
    @Transactional
    void getCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        // Get the cantiere
        restCantiereMockMvc
            .perform(get(ENTITY_API_URL_ID, cantiere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cantiere.getId().intValue()))
            .andExpect(jsonPath("$.nomeCantiere").value(DEFAULT_NOME_CANTIERE))
            .andExpect(jsonPath("$.indirizzo").value(DEFAULT_INDIRIZZO));
    }

    @Test
    @Transactional
    void getNonExistingCantiere() throws Exception {
        // Get the cantiere
        restCantiereMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere
        Cantiere updatedCantiere = cantiereRepository.findById(cantiere.getId()).get();
        // Disconnect from session so that the updates on updatedCantiere are not directly saved in db
        em.detach(updatedCantiere);
        updatedCantiere.nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCantiere.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(UPDATED_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
    }

    @Test
    @Transactional
    void putNonExistingCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cantiere.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCantiereWithPatch() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere using partial update
        Cantiere partialUpdatedCantiere = new Cantiere();
        partialUpdatedCantiere.setId(cantiere.getId());

        partialUpdatedCantiere.indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(DEFAULT_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
    }

    @Test
    @Transactional
    void fullUpdateCantiereWithPatch() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();

        // Update the cantiere using partial update
        Cantiere partialUpdatedCantiere = new Cantiere();
        partialUpdatedCantiere.setId(cantiere.getId());

        partialUpdatedCantiere.nomeCantiere(UPDATED_NOME_CANTIERE).indirizzo(UPDATED_INDIRIZZO);

        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCantiere))
            )
            .andExpect(status().isOk());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
        Cantiere testCantiere = cantiereList.get(cantiereList.size() - 1);
        assertThat(testCantiere.getNomeCantiere()).isEqualTo(UPDATED_NOME_CANTIERE);
        assertThat(testCantiere.getIndirizzo()).isEqualTo(UPDATED_INDIRIZZO);
    }

    @Test
    @Transactional
    void patchNonExistingCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cantiere.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cantiere))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCantiere() throws Exception {
        int databaseSizeBeforeUpdate = cantiereRepository.findAll().size();
        cantiere.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCantiereMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cantiere)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cantiere in the database
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCantiere() throws Exception {
        // Initialize the database
        cantiereRepository.saveAndFlush(cantiere);

        int databaseSizeBeforeDelete = cantiereRepository.findAll().size();

        // Delete the cantiere
        restCantiereMockMvc
            .perform(delete(ENTITY_API_URL_ID, cantiere.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cantiere> cantiereList = cantiereRepository.findAll();
        assertThat(cantiereList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
