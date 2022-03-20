package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.Lesson;
import com.spiashko.cm.domain.LessonDetails;
import com.spiashko.cm.repository.LessonDetailsRepository;
import com.spiashko.cm.repository.LessonRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link LessonDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonDetailsResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lesson-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LessonDetailsRepository lessonDetailsRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonDetailsMockMvc;

    private LessonDetails lessonDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonDetails createEntity(EntityManager em) {
        LessonDetails lessonDetails = new LessonDetails().content(DEFAULT_CONTENT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        lessonDetails.setLesson(lesson);
        return lessonDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonDetails createUpdatedEntity(EntityManager em) {
        LessonDetails lessonDetails = new LessonDetails().content(UPDATED_CONTENT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        lessonDetails.setLesson(lesson);
        return lessonDetails;
    }

    @BeforeEach
    public void initTest() {
        lessonDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createLessonDetails() throws Exception {
        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();
        // Create the LessonDetails
        restLessonDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isCreated());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        assertThat(testLessonDetails.getContent()).isEqualTo(DEFAULT_CONTENT);

        // Validate the id for MapsId, the ids must be same
        assertThat(testLessonDetails.getId()).isEqualTo(testLessonDetails.getLesson().getId());
    }

    @Test
    @Transactional
    void createLessonDetailsWithExistingId() throws Exception {
        // Create the LessonDetails with an existing ID
        lessonDetails.setId(1L);

        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateLessonDetailsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);
        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();

        // Add a new parent entity
        Lesson lesson = LessonResourceIT.createUpdatedEntity(em);
        em.persist(lesson);
        em.flush();

        // Load the lessonDetails
        LessonDetails updatedLessonDetails = lessonDetailsRepository.findById(lessonDetails.getId()).get();
        assertThat(updatedLessonDetails).isNotNull();
        // Disconnect from session so that the updates on updatedLessonDetails are not directly saved in db
        em.detach(updatedLessonDetails);

        // Update the Lesson with new association value
        updatedLessonDetails.setLesson(lesson);

        // Update the entity
        restLessonDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLessonDetails.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLessonDetails))
            )
            .andExpect(status().isOk());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeCreate);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testLessonDetails.getId()).isEqualTo(testLessonDetails.getLesson().getId());
    }

    @Test
    @Transactional
    void getAllLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        // Get all the lessonDetailsList
        restLessonDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }

    @Test
    @Transactional
    void getLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        // Get the lessonDetails
        restLessonDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, lessonDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonDetails.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLessonDetails() throws Exception {
        // Get the lessonDetails
        restLessonDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();

        // Update the lessonDetails
        LessonDetails updatedLessonDetails = lessonDetailsRepository.findById(lessonDetails.getId()).get();
        // Disconnect from session so that the updates on updatedLessonDetails are not directly saved in db
        em.detach(updatedLessonDetails);
        updatedLessonDetails.content(UPDATED_CONTENT);

        restLessonDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLessonDetails.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLessonDetails))
            )
            .andExpect(status().isOk());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        assertThat(testLessonDetails.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDetails.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonDetailsWithPatch() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();

        // Update the lessonDetails using partial update
        LessonDetails partialUpdatedLessonDetails = new LessonDetails();
        partialUpdatedLessonDetails.setId(lessonDetails.getId());

        partialUpdatedLessonDetails.content(UPDATED_CONTENT);

        restLessonDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonDetails.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonDetails))
            )
            .andExpect(status().isOk());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        assertThat(testLessonDetails.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdateLessonDetailsWithPatch() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();

        // Update the lessonDetails using partial update
        LessonDetails partialUpdatedLessonDetails = new LessonDetails();
        partialUpdatedLessonDetails.setId(lessonDetails.getId());

        partialUpdatedLessonDetails.content(UPDATED_CONTENT);

        restLessonDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonDetails.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonDetails))
            )
            .andExpect(status().isOk());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        assertThat(testLessonDetails.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonDetails.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();
        lessonDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeDelete = lessonDetailsRepository.findAll().size();

        // Delete the lessonDetails
        restLessonDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, lessonDetails.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
