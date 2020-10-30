package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.CompletedLesson;
import com.spiashko.cm.domain.Enrollment;
import com.spiashko.cm.repository.CompletedLessonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CompletedLessonResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class CompletedLessonResourceIT {

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
    public void createCompletedLesson() throws Exception {
        int databaseSizeBeforeCreate = completedLessonRepository.findAll().size();
        // Create the CompletedLesson
        restCompletedLessonMockMvc.perform(post("/api/completed-lessons").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(completedLesson)))
            .andExpect(status().isCreated());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeCreate + 1);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    public void createCompletedLessonWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = completedLessonRepository.findAll().size();

        // Create the CompletedLesson with an existing ID
        completedLesson.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompletedLessonMockMvc.perform(post("/api/completed-lessons").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(completedLesson)))
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCompletedLessons() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        // Get all the completedLessonList
        restCompletedLessonMockMvc.perform(get("/api/completed-lessons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(completedLesson.getId().intValue())));
    }
    
    @Test
    @Transactional
    public void getCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        // Get the completedLesson
        restCompletedLessonMockMvc.perform(get("/api/completed-lessons/{id}", completedLesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(completedLesson.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingCompletedLesson() throws Exception {
        // Get the completedLesson
        restCompletedLessonMockMvc.perform(get("/api/completed-lessons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();

        // Update the completedLesson
        CompletedLesson updatedCompletedLesson = completedLessonRepository.findById(completedLesson.getId()).get();
        // Disconnect from session so that the updates on updatedCompletedLesson are not directly saved in db
        em.detach(updatedCompletedLesson);

        restCompletedLessonMockMvc.perform(put("/api/completed-lessons").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompletedLesson)))
            .andExpect(status().isOk());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
        CompletedLesson testCompletedLesson = completedLessonList.get(completedLessonList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingCompletedLesson() throws Exception {
        int databaseSizeBeforeUpdate = completedLessonRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompletedLessonMockMvc.perform(put("/api/completed-lessons").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(completedLesson)))
            .andExpect(status().isBadRequest());

        // Validate the CompletedLesson in the database
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCompletedLesson() throws Exception {
        // Initialize the database
        completedLessonRepository.saveAndFlush(completedLesson);

        int databaseSizeBeforeDelete = completedLessonRepository.findAll().size();

        // Delete the completedLesson
        restCompletedLessonMockMvc.perform(delete("/api/completed-lessons/{id}", completedLesson.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CompletedLesson> completedLessonList = completedLessonRepository.findAll();
        assertThat(completedLessonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
