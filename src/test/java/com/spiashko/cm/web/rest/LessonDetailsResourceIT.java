package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.LessonDetails;
import com.spiashko.cm.domain.Lesson;
import com.spiashko.cm.repository.LessonDetailsRepository;
import com.spiashko.cm.repository.LessonRepository;

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
 * Integration tests for the {@link LessonDetailsResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class LessonDetailsResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

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
        LessonDetails lessonDetails = new LessonDetails()
            .content(DEFAULT_CONTENT);
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
        LessonDetails lessonDetails = new LessonDetails()
            .content(UPDATED_CONTENT);
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
    public void createLessonDetails() throws Exception {
        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();
        // Create the LessonDetails
        restLessonDetailsMockMvc.perform(post("/api/lesson-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lessonDetails)))
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
    public void createLessonDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();

        // Create the LessonDetails with an existing ID
        lessonDetails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonDetailsMockMvc.perform(post("/api/lesson-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lessonDetails)))
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateLessonDetailsMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);
        int databaseSizeBeforeCreate = lessonDetailsRepository.findAll().size();

        // Add a new parent entity
        Lesson lesson = LessonResourceIT.createUpdatedEntity(em);
        em.persist(lesson);
        em.flush();

        // Load the lessonDetails
        LessonDetails updatedLessonDetails = lessonDetailsRepository.findById(lessonDetails.getId()).get();
        // Disconnect from session so that the updates on updatedLessonDetails are not directly saved in db
        em.detach(updatedLessonDetails);

        // Update the Lesson with new association value
        updatedLessonDetails.setLesson(lesson);

        // Update the entity
        restLessonDetailsMockMvc.perform(put("/api/lesson-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLessonDetails)))
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
    public void getAllLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        // Get all the lessonDetailsList
        restLessonDetailsMockMvc.perform(get("/api/lesson-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }
    
    @Test
    @Transactional
    public void getLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        // Get the lessonDetails
        restLessonDetailsMockMvc.perform(get("/api/lesson-details/{id}", lessonDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonDetails.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingLessonDetails() throws Exception {
        // Get the lessonDetails
        restLessonDetailsMockMvc.perform(get("/api/lesson-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();

        // Update the lessonDetails
        LessonDetails updatedLessonDetails = lessonDetailsRepository.findById(lessonDetails.getId()).get();
        // Disconnect from session so that the updates on updatedLessonDetails are not directly saved in db
        em.detach(updatedLessonDetails);
        updatedLessonDetails
            .content(UPDATED_CONTENT);

        restLessonDetailsMockMvc.perform(put("/api/lesson-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLessonDetails)))
            .andExpect(status().isOk());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
        LessonDetails testLessonDetails = lessonDetailsList.get(lessonDetailsList.size() - 1);
        assertThat(testLessonDetails.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingLessonDetails() throws Exception {
        int databaseSizeBeforeUpdate = lessonDetailsRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonDetailsMockMvc.perform(put("/api/lesson-details").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(lessonDetails)))
            .andExpect(status().isBadRequest());

        // Validate the LessonDetails in the database
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLessonDetails() throws Exception {
        // Initialize the database
        lessonDetailsRepository.saveAndFlush(lessonDetails);

        int databaseSizeBeforeDelete = lessonDetailsRepository.findAll().size();

        // Delete the lessonDetails
        restLessonDetailsMockMvc.perform(delete("/api/lesson-details/{id}", lessonDetails.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LessonDetails> lessonDetailsList = lessonDetailsRepository.findAll();
        assertThat(lessonDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
