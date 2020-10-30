package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.CourseDetails;
import com.spiashko.cm.domain.Course;
import com.spiashko.cm.repository.CourseDetailsRepository;
import com.spiashko.cm.repository.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CourseDetailsResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class CourseDetailsResourceIT {

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    @Autowired
    private CourseDetailsRepository courseDetailsRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseDetailsMockMvc;

    private CourseDetails courseDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseDetails createEntity(EntityManager em) {
        CourseDetails courseDetails = new CourseDetails()
            .summary(DEFAULT_SUMMARY);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        courseDetails.setCourse(course);
        return courseDetails;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseDetails createUpdatedEntity(EntityManager em) {
        CourseDetails courseDetails = new CourseDetails()
            .summary(UPDATED_SUMMARY);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        courseDetails.setCourse(course);
        return courseDetails;
    }

    @BeforeEach
    public void initTest() {
        courseDetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createCourseDetails() throws Exception {
        int databaseSizeBeforeCreate = courseDetailsRepository.findAll().size();
        // Create the CourseDetails
        restCourseDetailsMockMvc.perform(post("/api/course-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseDetails)))
            .andExpect(status().isCreated());

        // Validate the CourseDetails in the database
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        CourseDetails testCourseDetails = courseDetailsList.get(courseDetailsList.size() - 1);
        assertThat(testCourseDetails.getSummary()).isEqualTo(DEFAULT_SUMMARY);

        // Validate the id for MapsId, the ids must be same
        assertThat(testCourseDetails.getId()).isEqualTo(testCourseDetails.getCourse().getId());
    }

    @Test
    @Transactional
    public void createCourseDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = courseDetailsRepository.findAll().size();

        // Create the CourseDetails with an existing ID
        courseDetails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseDetailsMockMvc.perform(post("/api/course-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseDetails)))
            .andExpect(status().isBadRequest());

        // Validate the CourseDetails in the database
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateCourseDetailsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        courseDetailsRepository.saveAndFlush(courseDetails);
        int databaseSizeBeforeCreate = courseDetailsRepository.findAll().size();

        // Add a new parent entity
        Course course = CourseResourceIT.createUpdatedEntity(em);
        em.persist(course);
        em.flush();

        // Load the courseDetails
        CourseDetails updatedCourseDetails = courseDetailsRepository.findById(courseDetails.getId()).get();
        // Disconnect from session so that the updates on updatedCourseDetails are not directly saved in db
        em.detach(updatedCourseDetails);

        // Update the Course with new association value
        updatedCourseDetails.setCourse(course);

        // Update the entity
        restCourseDetailsMockMvc.perform(put("/api/course-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseDetails)))
            .andExpect(status().isOk());

        // Validate the CourseDetails in the database
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeCreate);
        CourseDetails testCourseDetails = courseDetailsList.get(courseDetailsList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testCourseDetails.getId()).isEqualTo(testCourseDetails.getCourse().getId());
    }

    @Test
    @Transactional
    public void getAllCourseDetails() throws Exception {
        // Initialize the database
        courseDetailsRepository.saveAndFlush(courseDetails);

        // Get all the courseDetailsList
        restCourseDetailsMockMvc.perform(get("/api/course-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())));
    }
    
    @Test
    @Transactional
    public void getCourseDetails() throws Exception {
        // Initialize the database
        courseDetailsRepository.saveAndFlush(courseDetails);

        // Get the courseDetails
        restCourseDetailsMockMvc.perform(get("/api/course-details/{id}", courseDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseDetails.getId().intValue()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingCourseDetails() throws Exception {
        // Get the courseDetails
        restCourseDetailsMockMvc.perform(get("/api/course-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCourseDetails() throws Exception {
        // Initialize the database
        courseDetailsRepository.saveAndFlush(courseDetails);

        int databaseSizeBeforeUpdate = courseDetailsRepository.findAll().size();

        // Update the courseDetails
        CourseDetails updatedCourseDetails = courseDetailsRepository.findById(courseDetails.getId()).get();
        // Disconnect from session so that the updates on updatedCourseDetails are not directly saved in db
        em.detach(updatedCourseDetails);
        updatedCourseDetails
            .summary(UPDATED_SUMMARY);

        restCourseDetailsMockMvc.perform(put("/api/course-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseDetails)))
            .andExpect(status().isOk());

        // Validate the CourseDetails in the database
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeUpdate);
        CourseDetails testCourseDetails = courseDetailsList.get(courseDetailsList.size() - 1);
        assertThat(testCourseDetails.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    public void updateNonExistingCourseDetails() throws Exception {
        int databaseSizeBeforeUpdate = courseDetailsRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseDetailsMockMvc.perform(put("/api/course-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseDetails)))
            .andExpect(status().isBadRequest());

        // Validate the CourseDetails in the database
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCourseDetails() throws Exception {
        // Initialize the database
        courseDetailsRepository.saveAndFlush(courseDetails);

        int databaseSizeBeforeDelete = courseDetailsRepository.findAll().size();

        // Delete the courseDetails
        restCourseDetailsMockMvc.perform(delete("/api/course-details/{id}", courseDetails.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CourseDetails> courseDetailsList = courseDetailsRepository.findAll();
        assertThat(courseDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
