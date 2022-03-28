package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.Lesson;
import com.spiashko.cm.domain.LessonExtraInfo;
import com.spiashko.cm.repository.LessonExtraInfoRepository;
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
 * Integration tests for the {@link LessonExtraInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonExtraInfoResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lesson-extra-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LessonExtraInfoRepository lessonExtraInfoRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonExtraInfoMockMvc;

    private LessonExtraInfo lessonExtraInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonExtraInfo createEntity(EntityManager em) {
        LessonExtraInfo lessonExtraInfo = new LessonExtraInfo().content(DEFAULT_CONTENT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        lessonExtraInfo.setLesson(lesson);
        return lessonExtraInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonExtraInfo createUpdatedEntity(EntityManager em) {
        LessonExtraInfo lessonExtraInfo = new LessonExtraInfo().content(UPDATED_CONTENT);
        // Add required entity
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            lesson = LessonResourceIT.createUpdatedEntity(em);
            em.persist(lesson);
            em.flush();
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        lessonExtraInfo.setLesson(lesson);
        return lessonExtraInfo;
    }

    @BeforeEach
    public void initTest() {
        lessonExtraInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createLessonExtraInfo() throws Exception {
        int databaseSizeBeforeCreate = lessonExtraInfoRepository.findAll().size();
        // Create the LessonExtraInfo
        restLessonExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isCreated());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeCreate + 1);
        LessonExtraInfo testLessonExtraInfo = lessonExtraInfoList.get(lessonExtraInfoList.size() - 1);
        assertThat(testLessonExtraInfo.getContent()).isEqualTo(DEFAULT_CONTENT);

        // Validate the id for MapsId, the ids must be same
        assertThat(testLessonExtraInfo.getId()).isEqualTo(testLessonExtraInfo.getLesson().getId());
    }

    @Test
    @Transactional
    void createLessonExtraInfoWithExistingId() throws Exception {
        // Create the LessonExtraInfo with an existing ID
        lessonExtraInfo.setId(1L);

        int databaseSizeBeforeCreate = lessonExtraInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateLessonExtraInfoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);
        int databaseSizeBeforeCreate = lessonExtraInfoRepository.findAll().size();

        // Add a new parent entity
        Lesson lesson = LessonResourceIT.createUpdatedEntity(em);
        em.persist(lesson);
        em.flush();

        // Load the lessonExtraInfo
        LessonExtraInfo updatedLessonExtraInfo = lessonExtraInfoRepository.findById(lessonExtraInfo.getId()).get();
        assertThat(updatedLessonExtraInfo).isNotNull();
        // Disconnect from session so that the updates on updatedLessonExtraInfo are not directly saved in db
        em.detach(updatedLessonExtraInfo);

        // Update the Lesson with new association value
        updatedLessonExtraInfo.setLesson(lesson);

        // Update the entity
        restLessonExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLessonExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLessonExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeCreate);
        LessonExtraInfo testLessonExtraInfo = lessonExtraInfoList.get(lessonExtraInfoList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testLessonExtraInfo.getId()).isEqualTo(testLessonExtraInfo.getLesson().getId());
    }

    @Test
    @Transactional
    void getAllLessonExtraInfos() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        // Get all the lessonExtraInfoList
        restLessonExtraInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonExtraInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }

    @Test
    @Transactional
    void getLessonExtraInfo() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        // Get the lessonExtraInfo
        restLessonExtraInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, lessonExtraInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonExtraInfo.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLessonExtraInfo() throws Exception {
        // Get the lessonExtraInfo
        restLessonExtraInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLessonExtraInfo() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();

        // Update the lessonExtraInfo
        LessonExtraInfo updatedLessonExtraInfo = lessonExtraInfoRepository.findById(lessonExtraInfo.getId()).get();
        // Disconnect from session so that the updates on updatedLessonExtraInfo are not directly saved in db
        em.detach(updatedLessonExtraInfo);
        updatedLessonExtraInfo.content(UPDATED_CONTENT);

        restLessonExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLessonExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLessonExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        LessonExtraInfo testLessonExtraInfo = lessonExtraInfoList.get(lessonExtraInfoList.size() - 1);
        assertThat(testLessonExtraInfo.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonExtraInfoWithPatch() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();

        // Update the lessonExtraInfo using partial update
        LessonExtraInfo partialUpdatedLessonExtraInfo = new LessonExtraInfo();
        partialUpdatedLessonExtraInfo.setId(lessonExtraInfo.getId());

        restLessonExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        LessonExtraInfo testLessonExtraInfo = lessonExtraInfoList.get(lessonExtraInfoList.size() - 1);
        assertThat(testLessonExtraInfo.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdateLessonExtraInfoWithPatch() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();

        // Update the lessonExtraInfo using partial update
        LessonExtraInfo partialUpdatedLessonExtraInfo = new LessonExtraInfo();
        partialUpdatedLessonExtraInfo.setId(lessonExtraInfo.getId());

        partialUpdatedLessonExtraInfo.content(UPDATED_CONTENT);

        restLessonExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        LessonExtraInfo testLessonExtraInfo = lessonExtraInfoList.get(lessonExtraInfoList.size() - 1);
        assertThat(testLessonExtraInfo.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLessonExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = lessonExtraInfoRepository.findAll().size();
        lessonExtraInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonExtraInfo in the database
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLessonExtraInfo() throws Exception {
        // Initialize the database
        lessonExtraInfoRepository.saveAndFlush(lessonExtraInfo);

        int databaseSizeBeforeDelete = lessonExtraInfoRepository.findAll().size();

        // Delete the lessonExtraInfo
        restLessonExtraInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, lessonExtraInfo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LessonExtraInfo> lessonExtraInfoList = lessonExtraInfoRepository.findAll();
        assertThat(lessonExtraInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
