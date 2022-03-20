package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.CompletedLesson;
import com.spiashko.cm.domain.Enrollment;
import com.spiashko.cm.domain.Lesson;
import com.spiashko.cm.repository.CompletedLessonRepository;
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

/**
 * Integration tests for the {@link CompletedLessonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompletedLessonResourceIT {

    private static final String ENTITY_API_URL = "/api/completed-lessons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompletedLessonRepository completedLessonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompletedLessonMockMvc;

    private CompletedLesson completedLesson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompletedLesson createEntity(EntityManager em) {
        CompletedLesson completedLesson = new CompletedLesson();
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        completedLesson.setLesson(lesson);
        // Add required entity
        Enrollment enrollment;
        if (TestUtil.findAll(em, Enrollment.class).isEmpty()) {
            enrollment = EnrollmentResourceIT.createEntity(em);
            em.persist(enrollment);
            em.flush();
        } else {
            enrollment = TestUtil.findAll(em, Enrollment.class).get(0);
        }
        completedLesson.setEnrollment(enrollment);
        return completedLesson;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompletedLesson createUpdatedEntity(EntityManager em) {
        CompletedLesson completedLesson = new CompletedLesson();
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        completedLesson.setLesson(lesson);
        // Add required entity
        Enrollment enrollment;
        if (TestUtil.findAll(em, Enrollment.class).isEmpty()) {
            enrollment = EnrollmentResourceIT.createUpdatedEntity(em);
            em.persist(enrollment);
            em.flush();
        } else {
            enrollment = TestUtil.findAll(em, Enrollment.class).get(0);
        }
        completedLesson.setEnrollment(enrollment);
        return completedLesson;
    }

    @BeforeEach
    public void initTest() {
        completedLesson = createEntity(em);
    }

    @Test
    @Transactional
    void createCompletedLesson() throws Exception {
        int databaseSizeBeforeCreate = completedLessonRepository.findAll().size();
        // Create the CompletedLesson
        restCompletedLessonMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isCreated());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeCreate + 1);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    void createCompletedLessonWithExistingId() throws Exception {
        // Create the CompletedLesson with an existing ID
        completedLesson.setId(1L);

        int databaseSizeBeforeCreate = completedLessonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompletedLessonMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCompletedLessons() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        // Get all the completedLessonList
        restCompletedLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(completedLesson.getId().intValue())));
    }

    @Test
    @Transactional
    void getCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        // Get the completedLesson
        restCompletedLessonMockMvc
            .perform(get(ENTITY_API_URL_ID, completedLesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(completedLesson.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingCompletedLesson() throws Exception {
        // Get the completedLesson
        restCompletedLessonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();

        // Update the completedLesson
        CompletedLesson updatedCompletedLesson = completedLessonRepository.findById(completedLesson.getId()).get();
        // Disconnect from session so that the updates on updatedCompletedLesson are not directly saved in db
        em.detach(updatedCompletedLesson);

        restCompletedLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCompletedLesson.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCompletedLesson))
            )
            .andExpect(status().isOk());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, completedLesson.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompletedLessonWithPatch() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();

        // Update the completedLesson using partial update
        CompletedLesson partialUpdatedCompletedLesson = new CompletedLesson();
        partialUpdatedCompletedLesson.setId(completedLesson.getId());

        restCompletedLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompletedLesson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompletedLesson))
            )
            .andExpect(status().isOk());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCompletedLessonWithPatch() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();

        // Update the completedLesson using partial update
        CompletedLesson partialUpdatedCompletedLesson = new CompletedLesson();
        partialUpdatedCompletedLesson.setId(completedLesson.getId());

        restCompletedLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompletedLesson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompletedLesson))
            )
            .andExpect(status().isOk());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, completedLesson.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();
        completedLesson.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(completedLesson))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeDelete = completedLessonRepository.findAll().size();

        // Delete the completedLesson
        restCompletedLessonMockMvc
            .perform(delete(ENTITY_API_URL_ID, completedLesson.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
