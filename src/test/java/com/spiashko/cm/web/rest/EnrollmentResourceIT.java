package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.Course;
import com.spiashko.cm.domain.Enrollment;
import com.spiashko.cm.domain.User;
import com.spiashko.cm.repository.EnrollmentRepository;
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
 * Integration tests for the {@link EnrollmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnrollmentResourceIT {

    private static final String ENTITY_API_URL = "/api/enrollments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnrollmentMockMvc;

    private Enrollment enrollment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createEntity(EntityManager em) {
        Enrollment enrollment = new Enrollment();
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        enrollment.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        enrollment.setStudent(user);
        return enrollment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Enrollment createUpdatedEntity(EntityManager em) {
        Enrollment enrollment = new Enrollment();
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        enrollment.setCourse(course);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        enrollment.setStudent(user);
        return enrollment;
    }

    @BeforeEach
    public void initTest() {
        enrollment = createEntity(em);
    }

    @Test
    @Transactional
    void createEnrollment() throws Exception {
        int databaseSizeBeforeCreate = enrollmentRepository.findAll().size();
        // Create the Enrollment
        restEnrollmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isCreated());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeCreate + 1);
        Enrollment testEnrollment = enrollmentList.get(enrollmentList.size() - 1);
    }

    @Test
    @Transactional
    void createEnrollmentWithExistingId() throws Exception {
        // Create the Enrollment with an existing ID
        enrollment.setId(1L);

        int databaseSizeBeforeCreate = enrollmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnrollmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnrollments() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        // Get all the enrollmentList
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(enrollment.getId().intValue())));
    }

    @Test
    @Transactional
    void getEnrollment() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        // Get the enrollment
        restEnrollmentMockMvc
            .perform(get(ENTITY_API_URL_ID, enrollment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(enrollment.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingEnrollment() throws Exception {
        // Get the enrollment
        restEnrollmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEnrollment() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();

        // Update the enrollment
        Enrollment updatedEnrollment = enrollmentRepository.findById(enrollment.getId()).get();
        // Disconnect from session so that the updates on updatedEnrollment are not directly saved in db
        em.detach(updatedEnrollment);

        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnrollment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
        Enrollment testEnrollment = enrollmentList.get(enrollmentList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enrollment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
        Enrollment testEnrollment = enrollmentList.get(enrollmentList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateEnrollmentWithPatch() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();

        // Update the enrollment using partial update
        Enrollment partialUpdatedEnrollment = new Enrollment();
        partialUpdatedEnrollment.setId(enrollment.getId());

        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnrollment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnrollment))
            )
            .andExpect(status().isOk());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
        Enrollment testEnrollment = enrollmentList.get(enrollmentList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enrollment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnrollment() throws Exception {
        int databaseSizeBeforeUpdate = enrollmentRepository.findAll().size();
        enrollment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnrollmentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enrollment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Enrollment in the database
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnrollment() throws Exception {
        // Initialize the database
        enrollmentRepository.saveAndFlush(enrollment);

        int databaseSizeBeforeDelete = enrollmentRepository.findAll().size();

        // Delete the enrollment
        restEnrollmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, enrollment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Enrollment> enrollmentList = enrollmentRepository.findAll();
        assertThat(enrollmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
