package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.Logo;
import com.spiashko.cm.repository.CourseLogoRepository;
import com.spiashko.cm.repository.LogoRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CourseLogoResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class CourseLogoResourceIT {

    @Autowired
    private CourseLogoRepository courseLogoRepository;
    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseLogoMockMvc;

    private CourseLogo courseLogo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseLogo createEntity(EntityManager em) {
        CourseLogo courseLogo = new CourseLogo();
        // Add required entity
        Logo logo;
        if (TestUtil.findAll(em, Logo.class).isEmpty()) {
            logo = LogoResourceIT.createEntity(em);
            em.persist(logo);
            em.flush();
        } else {
            logo = TestUtil.findAll(em, Logo.class).get(0);
        }
        courseLogo.setLogo(logo);
        return courseLogo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseLogo createUpdatedEntity(EntityManager em) {
        CourseLogo courseLogo = new CourseLogo();
        // Add required entity
        Logo logo;
        if (TestUtil.findAll(em, Logo.class).isEmpty()) {
            logo = LogoResourceIT.createUpdatedEntity(em);
            em.persist(logo);
            em.flush();
        } else {
            logo = TestUtil.findAll(em, Logo.class).get(0);
        }
        courseLogo.setLogo(logo);
        return courseLogo;
    }

    @BeforeEach
    public void initTest() {
        courseLogo = createEntity(em);
    }

    @Test
    @Transactional
    public void createCourseLogo() throws Exception {
        int databaseSizeBeforeCreate = courseLogoRepository.findAll().size();
        // Create the CourseLogo
        restCourseLogoMockMvc.perform(post("/api/course-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseLogo)))
            .andExpect(status().isCreated());

        // Validate the CourseLogo in the database
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeCreate + 1);
        CourseLogo testCourseLogo = courseLogoList.get(courseLogoList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        assertThat(testCourseLogo.getId()).isEqualTo(testCourseLogo.getLogo().getId());
    }

    @Test
    @Transactional
    public void createCourseLogoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = courseLogoRepository.findAll().size();

        // Create the CourseLogo with an existing ID
        courseLogo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseLogoMockMvc.perform(post("/api/course-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseLogo)))
            .andExpect(status().isBadRequest());

        // Validate the CourseLogo in the database
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateCourseLogoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        courseLogoRepository.saveAndFlush(courseLogo);
        int databaseSizeBeforeCreate = courseLogoRepository.findAll().size();

        // Add a new parent entity
        Logo logo = LogoResourceIT.createUpdatedEntity(em);
        em.persist(logo);
        em.flush();

        // Load the courseLogo
        CourseLogo updatedCourseLogo = courseLogoRepository.findById(courseLogo.getId()).get();
        // Disconnect from session so that the updates on updatedCourseLogo are not directly saved in db
        em.detach(updatedCourseLogo);

        // Update the Logo with new association value
        updatedCourseLogo.setLogo(logo);

        // Update the entity
        restCourseLogoMockMvc.perform(put("/api/course-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseLogo)))
            .andExpect(status().isOk());

        // Validate the CourseLogo in the database
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeCreate);
        CourseLogo testCourseLogo = courseLogoList.get(courseLogoList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testCourseLogo.getId()).isEqualTo(testCourseLogo.getLogo().getId());
    }

    @Test
    @Transactional
    public void getAllCourseLogos() throws Exception {
        // Initialize the database
        courseLogoRepository.saveAndFlush(courseLogo);

        // Get all the courseLogoList
        restCourseLogoMockMvc.perform(get("/api/course-logos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseLogo.getId().intValue())));
    }

    @Test
    @Transactional
    public void getCourseLogo() throws Exception {
        // Initialize the database
        courseLogoRepository.saveAndFlush(courseLogo);

        // Get the courseLogo
        restCourseLogoMockMvc.perform(get("/api/course-logos/{id}", courseLogo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseLogo.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingCourseLogo() throws Exception {
        // Get the courseLogo
        restCourseLogoMockMvc.perform(get("/api/course-logos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCourseLogo() throws Exception {
        // Initialize the database
        courseLogoRepository.saveAndFlush(courseLogo);

        int databaseSizeBeforeUpdate = courseLogoRepository.findAll().size();

        // Update the courseLogo
        CourseLogo updatedCourseLogo = courseLogoRepository.findById(courseLogo.getId()).get();
        // Disconnect from session so that the updates on updatedCourseLogo are not directly saved in db
        em.detach(updatedCourseLogo);

        restCourseLogoMockMvc.perform(put("/api/course-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseLogo)))
            .andExpect(status().isOk());

        // Validate the CourseLogo in the database
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeUpdate);
        CourseLogo testCourseLogo = courseLogoList.get(courseLogoList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingCourseLogo() throws Exception {
        int databaseSizeBeforeUpdate = courseLogoRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseLogoMockMvc.perform(put("/api/course-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(courseLogo)))
            .andExpect(status().isBadRequest());

        // Validate the CourseLogo in the database
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCourseLogo() throws Exception {
        // Initialize the database
        courseLogoRepository.saveAndFlush(courseLogo);

        int databaseSizeBeforeDelete = courseLogoRepository.findAll().size();

        // Delete the courseLogo
        restCourseLogoMockMvc.perform(delete("/api/course-logos/{id}", courseLogo.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CourseLogo> courseLogoList = courseLogoRepository.findAll();
        assertThat(courseLogoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
