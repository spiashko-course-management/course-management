package com.spiashko.cm.web.rest;

import com.spiashko.cm.CourseManagementApp;
import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.domain.Artifact;
import com.spiashko.cm.repository.ArtifactRepository;

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
 * Integration tests for the {@link ArtifactResource} REST controller.
 */
@SpringBootTest(classes = { CourseManagementApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class ArtifactResourceIT {

    private static final byte[] DEFAULT_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_ORIGINAL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_NAME = "BBBBBBBBBB";

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArtifactMockMvc;

    private Artifact artifact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artifact createEntity(EntityManager em) {
        Artifact artifact = new Artifact()
            .data(DEFAULT_DATA)
            .dataContentType(DEFAULT_DATA_CONTENT_TYPE)
            .originalName(DEFAULT_ORIGINAL_NAME);
        return artifact;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artifact createUpdatedEntity(EntityManager em) {
        Artifact artifact = new Artifact()
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .originalName(UPDATED_ORIGINAL_NAME);
        return artifact;
    }

    @BeforeEach
    public void initTest() {
        artifact = createEntity(em);
    }

    @Test
    @Transactional
    public void createArtifact() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();
        // Create the Artifact
        restArtifactMockMvc.perform(post("/api/artifacts").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isCreated());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate + 1);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testArtifact.getDataContentType()).isEqualTo(DEFAULT_DATA_CONTENT_TYPE);
        assertThat(testArtifact.getOriginalName()).isEqualTo(DEFAULT_ORIGINAL_NAME);
    }

    @Test
    @Transactional
    public void createArtifactWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = artifactRepository.findAll().size();

        // Create the Artifact with an existing ID
        artifact.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtifactMockMvc.perform(post("/api/artifacts").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkOriginalNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artifactRepository.findAll().size();
        // set the field null
        artifact.setOriginalName(null);

        // Create the Artifact, which fails.


        restArtifactMockMvc.perform(post("/api/artifacts").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isBadRequest());

        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllArtifacts() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        // Get all the artifactList
        restArtifactMockMvc.perform(get("/api/artifacts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artifact.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataContentType").value(hasItem(DEFAULT_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA))))
            .andExpect(jsonPath("$.[*].originalName").value(hasItem(DEFAULT_ORIGINAL_NAME)));
    }
    
    @Test
    @Transactional
    public void getArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", artifact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(artifact.getId().intValue()))
            .andExpect(jsonPath("$.dataContentType").value(DEFAULT_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.data").value(Base64Utils.encodeToString(DEFAULT_DATA)))
            .andExpect(jsonPath("$.originalName").value(DEFAULT_ORIGINAL_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingArtifact() throws Exception {
        // Get the artifact
        restArtifactMockMvc.perform(get("/api/artifacts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // Update the artifact
        Artifact updatedArtifact = artifactRepository.findById(artifact.getId()).get();
        // Disconnect from session so that the updates on updatedArtifact are not directly saved in db
        em.detach(updatedArtifact);
        updatedArtifact
            .data(UPDATED_DATA)
            .dataContentType(UPDATED_DATA_CONTENT_TYPE)
            .originalName(UPDATED_ORIGINAL_NAME);

        restArtifactMockMvc.perform(put("/api/artifacts").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedArtifact)))
            .andExpect(status().isOk());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate);
        Artifact testArtifact = artifactList.get(artifactList.size() - 1);
        assertThat(testArtifact.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testArtifact.getDataContentType()).isEqualTo(UPDATED_DATA_CONTENT_TYPE);
        assertThat(testArtifact.getOriginalName()).isEqualTo(UPDATED_ORIGINAL_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingArtifact() throws Exception {
        int databaseSizeBeforeUpdate = artifactRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtifactMockMvc.perform(put("/api/artifacts").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(artifact)))
            .andExpect(status().isBadRequest());

        // Validate the Artifact in the database
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteArtifact() throws Exception {
        // Initialize the database
        artifactRepository.saveAndFlush(artifact);

        int databaseSizeBeforeDelete = artifactRepository.findAll().size();

        // Delete the artifact
        restArtifactMockMvc.perform(delete("/api/artifacts/{id}", artifact.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Artifact> artifactList = artifactRepository.findAll();
        assertThat(artifactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
