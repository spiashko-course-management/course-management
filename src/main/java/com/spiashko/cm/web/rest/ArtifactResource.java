package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.Artifact;
import com.spiashko.cm.repository.ArtifactRepository;
import com.spiashko.cm.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.Artifact}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ArtifactResource {

    private final Logger log = LoggerFactory.getLogger(ArtifactResource.class);

    private static final String ENTITY_NAME = "artifact";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtifactRepository artifactRepository;

    public ArtifactResource(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    /**
     * {@code POST  /artifacts} : Create a new artifact.
     *
     * @param artifact the artifact to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new artifact, or with status {@code 400 (Bad Request)} if the artifact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/artifacts")
    public ResponseEntity<Artifact> createArtifact(@Valid @RequestBody Artifact artifact) throws URISyntaxException {
        log.debug("REST request to save Artifact : {}", artifact);
        if (artifact.getId() != null) {
            throw new BadRequestAlertException("A new artifact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Artifact result = artifactRepository.save(artifact);
        return ResponseEntity.created(new URI("/api/artifacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /artifacts} : Updates an existing artifact.
     *
     * @param artifact the artifact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artifact,
     * or with status {@code 400 (Bad Request)} if the artifact is not valid,
     * or with status {@code 500 (Internal Server Error)} if the artifact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/artifacts")
    public ResponseEntity<Artifact> updateArtifact(@Valid @RequestBody Artifact artifact) throws URISyntaxException {
        log.debug("REST request to update Artifact : {}", artifact);
        if (artifact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Artifact result = artifactRepository.save(artifact);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, artifact.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /artifacts} : get all the artifacts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of artifacts in body.
     */
    @GetMapping("/artifacts")
    public List<Artifact> getAllArtifacts() {
        log.debug("REST request to get all Artifacts");
        return artifactRepository.findAll();
    }

    /**
     * {@code GET  /artifacts/:id} : get the "id" artifact.
     *
     * @param id the id of the artifact to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the artifact, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/artifacts/{id}")
    public ResponseEntity<Artifact> getArtifact(@PathVariable Long id) {
        log.debug("REST request to get Artifact : {}", id);
        Optional<Artifact> artifact = artifactRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(artifact);
    }

    /**
     * {@code DELETE  /artifacts/:id} : delete the "id" artifact.
     *
     * @param id the id of the artifact to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/artifacts/{id}")
    public ResponseEntity<Void> deleteArtifact(@PathVariable Long id) {
        log.debug("REST request to delete Artifact : {}", id);
        artifactRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
