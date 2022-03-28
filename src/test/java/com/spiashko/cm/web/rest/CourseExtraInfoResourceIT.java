package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.Course;
import com.spiashko.cm.domain.CourseExtraInfo;
import com.spiashko.cm.repository.CourseExtraInfoRepository;
import com.spiashko.cm.repository.CourseRepository;
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
 * Integration tests for the {@link CourseExtraInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CourseExtraInfoResourceIT {

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/course-extra-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseExtraInfoRepository courseExtraInfoRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseExtraInfoMockMvc;

    private CourseExtraInfo courseExtraInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseExtraInfo createEntity(EntityManager em) {
        CourseExtraInfo courseExtraInfo = new CourseExtraInfo().summary(DEFAULT_SUMMARY);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        courseExtraInfo.setCourse(course);
        return courseExtraInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseExtraInfo createUpdatedEntity(EntityManager em) {
        CourseExtraInfo courseExtraInfo = new CourseExtraInfo().summary(UPDATED_SUMMARY);
        // Add required entity
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            course = CourseResourceIT.createUpdatedEntity(em);
            em.persist(course);
            em.flush();
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        courseExtraInfo.setCourse(course);
        return courseExtraInfo;
    }

    @BeforeEach
    public void initTest() {
        courseExtraInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createCourseExtraInfo() throws Exception {
        int databaseSizeBeforeCreate = courseExtraInfoRepository.findAll().size();
        // Create the CourseExtraInfo
        restCourseExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isCreated());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeCreate + 1);
        CourseExtraInfo testCourseExtraInfo = courseExtraInfoList.get(courseExtraInfoList.size() - 1);
        assertThat(testCourseExtraInfo.getSummary()).isEqualTo(DEFAULT_SUMMARY);

        // Validate the id for MapsId, the ids must be same
        assertThat(testCourseExtraInfo.getId()).isEqualTo(testCourseExtraInfo.getCourse().getId());
    }

    @Test
    @Transactional
    void createCourseExtraInfoWithExistingId() throws Exception {
        // Create the CourseExtraInfo with an existing ID
        courseExtraInfo.setId(1L);

        int databaseSizeBeforeCreate = courseExtraInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateCourseExtraInfoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);
        int databaseSizeBeforeCreate = courseExtraInfoRepository.findAll().size();

        // Add a new parent entity
        Course course = CourseResourceIT.createUpdatedEntity(em);
        em.persist(course);
        em.flush();

        // Load the courseExtraInfo
        CourseExtraInfo updatedCourseExtraInfo = courseExtraInfoRepository.findById(courseExtraInfo.getId()).get();
        assertThat(updatedCourseExtraInfo).isNotNull();
        // Disconnect from session so that the updates on updatedCourseExtraInfo are not directly saved in db
        em.detach(updatedCourseExtraInfo);

        // Update the Course with new association value
        updatedCourseExtraInfo.setCourse(course);

        // Update the entity
        restCourseExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourseExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCourseExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeCreate);
        CourseExtraInfo testCourseExtraInfo = courseExtraInfoList.get(courseExtraInfoList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testCourseExtraInfo.getId()).isEqualTo(testCourseExtraInfo.getCourse().getId());
    }

    @Test
    @Transactional
    void getAllCourseExtraInfos() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        // Get all the courseExtraInfoList
        restCourseExtraInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseExtraInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())));
    }

    @Test
    @Transactional
    void getCourseExtraInfo() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        // Get the courseExtraInfo
        restCourseExtraInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, courseExtraInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(courseExtraInfo.getId().intValue()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCourseExtraInfo() throws Exception {
        // Get the courseExtraInfo
        restCourseExtraInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCourseExtraInfo() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();

        // Update the courseExtraInfo
        CourseExtraInfo updatedCourseExtraInfo = courseExtraInfoRepository.findById(courseExtraInfo.getId()).get();
        // Disconnect from session so that the updates on updatedCourseExtraInfo are not directly saved in db
        em.detach(updatedCourseExtraInfo);
        updatedCourseExtraInfo.summary(UPDATED_SUMMARY);

        restCourseExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCourseExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCourseExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        CourseExtraInfo testCourseExtraInfo = courseExtraInfoList.get(courseExtraInfoList.size() - 1);
        assertThat(testCourseExtraInfo.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void putNonExistingCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseExtraInfoWithPatch() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();

        // Update the courseExtraInfo using partial update
        CourseExtraInfo partialUpdatedCourseExtraInfo = new CourseExtraInfo();
        partialUpdatedCourseExtraInfo.setId(courseExtraInfo.getId());

        partialUpdatedCourseExtraInfo.summary(UPDATED_SUMMARY);

        restCourseExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourseExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        CourseExtraInfo testCourseExtraInfo = courseExtraInfoList.get(courseExtraInfoList.size() - 1);
        assertThat(testCourseExtraInfo.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void fullUpdateCourseExtraInfoWithPatch() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();

        // Update the courseExtraInfo using partial update
        CourseExtraInfo partialUpdatedCourseExtraInfo = new CourseExtraInfo();
        partialUpdatedCourseExtraInfo.setId(courseExtraInfo.getId());

        partialUpdatedCourseExtraInfo.summary(UPDATED_SUMMARY);

        restCourseExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourseExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourseExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        CourseExtraInfo testCourseExtraInfo = courseExtraInfoList.get(courseExtraInfoList.size() - 1);
        assertThat(testCourseExtraInfo.getSummary()).isEqualTo(UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void patchNonExistingCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourseExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = courseExtraInfoRepository.findAll().size();
        courseExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CourseExtraInfo in the database
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourseExtraInfo() throws Exception {
        // Initialize the database
        courseExtraInfoRepository.saveAndFlush(courseExtraInfo);

        int databaseSizeBeforeDelete = courseExtraInfoRepository.findAll().size();

        // Delete the courseExtraInfo
        restCourseExtraInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, courseExtraInfo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CourseExtraInfo> courseExtraInfoList = courseExtraInfoRepository.findAll();
        assertThat(courseExtraInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
