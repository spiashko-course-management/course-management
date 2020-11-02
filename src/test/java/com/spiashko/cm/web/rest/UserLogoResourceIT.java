package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.Logo;
import com.spiashko.cm.repository.LogoRepository;
import com.spiashko.cm.repository.UserLogoRepository;
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
 * Integration tests for the {@link UserLogoResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class UserLogoResourceIT {

    @Autowired
    private UserLogoRepository userLogoRepository;
    @Autowired
    private LogoRepository logoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserLogoMockMvc;

    private UserLogo userLogo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogo createEntity(EntityManager em) {
        UserLogo userLogo = new UserLogo();
        // Add required entity
        Logo logo;
        if (TestUtil.findAll(em, Logo.class).isEmpty()) {
            logo = LogoResourceIT.createEntity(em);
            em.persist(logo);
            em.flush();
        } else {
            logo = TestUtil.findAll(em, Logo.class).get(0);
        }
        userLogo.setLogo(logo);
        return userLogo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserLogo createUpdatedEntity(EntityManager em) {
        UserLogo userLogo = new UserLogo();
        // Add required entity
        Logo logo;
        if (TestUtil.findAll(em, Logo.class).isEmpty()) {
            logo = LogoResourceIT.createUpdatedEntity(em);
            em.persist(logo);
            em.flush();
        } else {
            logo = TestUtil.findAll(em, Logo.class).get(0);
        }
        userLogo.setLogo(logo);
        return userLogo;
    }

    @BeforeEach
    public void initTest() {
        userLogo = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserLogo() throws Exception {
        int databaseSizeBeforeCreate = userLogoRepository.findAll().size();
        // Create the UserLogo
        restUserLogoMockMvc.perform(post("/api/user-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogo)))
            .andExpect(status().isCreated());

        // Validate the UserLogo in the database
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeCreate + 1);
        UserLogo testUserLogo = userLogoList.get(userLogoList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        assertThat(testUserLogo.getId()).isEqualTo(testUserLogo.getLogo().getId());
    }

    @Test
    @Transactional
    public void createUserLogoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userLogoRepository.findAll().size();

        // Create the UserLogo with an existing ID
        userLogo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserLogoMockMvc.perform(post("/api/user-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogo)))
            .andExpect(status().isBadRequest());

        // Validate the UserLogo in the database
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void updateUserLogoMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        userLogoRepository.saveAndFlush(userLogo);
        int databaseSizeBeforeCreate = userLogoRepository.findAll().size();

        // Add a new parent entity
        Logo logo = LogoResourceIT.createUpdatedEntity(em);
        em.persist(logo);
        em.flush();

        // Load the userLogo
        UserLogo updatedUserLogo = userLogoRepository.findById(userLogo.getId()).get();
        // Disconnect from session so that the updates on updatedUserLogo are not directly saved in db
        em.detach(updatedUserLogo);

        // Update the Logo with new association value
        updatedUserLogo.setLogo(logo);

        // Update the entity
        restUserLogoMockMvc.perform(put("/api/user-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserLogo)))
            .andExpect(status().isOk());

        // Validate the UserLogo in the database
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeCreate);
        UserLogo testUserLogo = userLogoList.get(userLogoList.size() - 1);

        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testUserLogo.getId()).isEqualTo(testUserLogo.getLogo().getId());
    }

    @Test
    @Transactional
    public void getAllUserLogos() throws Exception {
        // Initialize the database
        userLogoRepository.saveAndFlush(userLogo);

        // Get all the userLogoList
        restUserLogoMockMvc.perform(get("/api/user-logos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userLogo.getId().intValue())));
    }

    @Test
    @Transactional
    public void getUserLogo() throws Exception {
        // Initialize the database
        userLogoRepository.saveAndFlush(userLogo);

        // Get the userLogo
        restUserLogoMockMvc.perform(get("/api/user-logos/{id}", userLogo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userLogo.getId().intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingUserLogo() throws Exception {
        // Get the userLogo
        restUserLogoMockMvc.perform(get("/api/user-logos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserLogo() throws Exception {
        // Initialize the database
        userLogoRepository.saveAndFlush(userLogo);

        int databaseSizeBeforeUpdate = userLogoRepository.findAll().size();

        // Update the userLogo
        UserLogo updatedUserLogo = userLogoRepository.findById(userLogo.getId()).get();
        // Disconnect from session so that the updates on updatedUserLogo are not directly saved in db
        em.detach(updatedUserLogo);

        restUserLogoMockMvc.perform(put("/api/user-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserLogo)))
            .andExpect(status().isOk());

        // Validate the UserLogo in the database
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeUpdate);
        UserLogo testUserLogo = userLogoList.get(userLogoList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingUserLogo() throws Exception {
        int databaseSizeBeforeUpdate = userLogoRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserLogoMockMvc.perform(put("/api/user-logos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userLogo)))
            .andExpect(status().isBadRequest());

        // Validate the UserLogo in the database
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUserLogo() throws Exception {
        // Initialize the database
        userLogoRepository.saveAndFlush(userLogo);

        int databaseSizeBeforeDelete = userLogoRepository.findAll().size();

        // Delete the userLogo
        restUserLogoMockMvc.perform(delete("/api/user-logos/{id}", userLogo.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserLogo> userLogoList = userLogoRepository.findAll();
        assertThat(userLogoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
