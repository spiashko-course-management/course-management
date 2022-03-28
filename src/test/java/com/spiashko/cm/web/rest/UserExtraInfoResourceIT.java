package com.spiashko.cm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.domain.User;
import com.spiashko.cm.domain.UserExtraInfo;
import com.spiashko.cm.repository.UserExtraInfoRepository;
import com.spiashko.cm.repository.UserRepository;
import java.util.List;
import java.util.UUID;
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
 * Integration tests for the {@link UserExtraInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserExtraInfoResourceIT {

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-extra-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private UserExtraInfoRepository userExtraInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserExtraInfoMockMvc;

    private UserExtraInfo userExtraInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtraInfo createEntity(EntityManager em) {
        UserExtraInfo userExtraInfo = new UserExtraInfo().bio(DEFAULT_BIO);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userExtraInfo.setUser(user);
        return userExtraInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserExtraInfo createUpdatedEntity(EntityManager em) {
        UserExtraInfo userExtraInfo = new UserExtraInfo().bio(UPDATED_BIO);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userExtraInfo.setUser(user);
        return userExtraInfo;
    }

    @BeforeEach
    public void initTest() {
        userExtraInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createUserExtraInfo() throws Exception {
        int databaseSizeBeforeCreate = userExtraInfoRepository.findAll().size();
        // Create the UserExtraInfo
        restUserExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isCreated());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserExtraInfo testUserExtraInfo = userExtraInfoList.get(userExtraInfoList.size() - 1);
        assertThat(testUserExtraInfo.getBio()).isEqualTo(DEFAULT_BIO);

        // Validate the id for MapsId, the ids must be same
        assertThat(testUserExtraInfo.getId()).isEqualTo(testUserExtraInfo.getUser().getId());
    }

    @Test
    @Transactional
    void createUserExtraInfoWithExistingId() throws Exception {
        // Create the UserExtraInfo with an existing ID
        userExtraInfo.setId("existing_id");

        int databaseSizeBeforeCreate = userExtraInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserExtraInfoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateUserExtraInfoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);
        int databaseSizeBeforeCreate = userExtraInfoRepository.findAll().size();

        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the userExtraInfo
        UserExtraInfo updatedUserExtraInfo = userExtraInfoRepository.findById(userExtraInfo.getId()).get();
        assertThat(updatedUserExtraInfo).isNotNull();
        // Disconnect from session so that the updates on updatedUserExtraInfo are not directly saved in db
        em.detach(updatedUserExtraInfo);

        // Update the User with new association value
        updatedUserExtraInfo.setUser(user);

        // Update the entity
        restUserExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeCreate);
        UserExtraInfo testUserExtraInfo = userExtraInfoList.get(userExtraInfoList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testUserExtraInfo.getId()).isEqualTo(testUserExtraInfo.getUser().getId());
    }

    @Test
    @Transactional
    void getAllUserExtraInfos() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        // Get all the userExtraInfoList
        restUserExtraInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userExtraInfo.getId())))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO.toString())));
    }

    @Test
    @Transactional
    void getUserExtraInfo() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        // Get the userExtraInfo
        restUserExtraInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, userExtraInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userExtraInfo.getId()))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserExtraInfo() throws Exception {
        // Get the userExtraInfo
        restUserExtraInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserExtraInfo() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();

        // Update the userExtraInfo
        UserExtraInfo updatedUserExtraInfo = userExtraInfoRepository.findById(userExtraInfo.getId()).get();
        // Disconnect from session so that the updates on updatedUserExtraInfo are not directly saved in db
        em.detach(updatedUserExtraInfo);
        updatedUserExtraInfo.bio(UPDATED_BIO);

        restUserExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        UserExtraInfo testUserExtraInfo = userExtraInfoList.get(userExtraInfoList.size() - 1);
        assertThat(testUserExtraInfo.getBio()).isEqualTo(UPDATED_BIO);
    }

    @Test
    @Transactional
    void putNonExistingUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userExtraInfo.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserExtraInfoWithPatch() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();

        // Update the userExtraInfo using partial update
        UserExtraInfo partialUpdatedUserExtraInfo = new UserExtraInfo();
        partialUpdatedUserExtraInfo.setId(userExtraInfo.getId());

        restUserExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        UserExtraInfo testUserExtraInfo = userExtraInfoList.get(userExtraInfoList.size() - 1);
        assertThat(testUserExtraInfo.getBio()).isEqualTo(DEFAULT_BIO);
    }

    @Test
    @Transactional
    void fullUpdateUserExtraInfoWithPatch() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();

        // Update the userExtraInfo using partial update
        UserExtraInfo partialUpdatedUserExtraInfo = new UserExtraInfo();
        partialUpdatedUserExtraInfo.setId(userExtraInfo.getId());

        partialUpdatedUserExtraInfo.bio(UPDATED_BIO);

        restUserExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserExtraInfo))
            )
            .andExpect(status().isOk());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
        UserExtraInfo testUserExtraInfo = userExtraInfoList.get(userExtraInfoList.size() - 1);
        assertThat(testUserExtraInfo.getBio()).isEqualTo(UPDATED_BIO);
    }

    @Test
    @Transactional
    void patchNonExistingUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userExtraInfo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserExtraInfo() throws Exception {
        int databaseSizeBeforeUpdate = userExtraInfoRepository.findAll().size();
        userExtraInfo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserExtraInfoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userExtraInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserExtraInfo in the database
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserExtraInfo() throws Exception {
        // Initialize the database
        userExtraInfoRepository.saveAndFlush(userExtraInfo);

        int databaseSizeBeforeDelete = userExtraInfoRepository.findAll().size();

        // Delete the userExtraInfo
        restUserExtraInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, userExtraInfo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserExtraInfo> userExtraInfoList = userExtraInfoRepository.findAll();
        assertThat(userExtraInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
