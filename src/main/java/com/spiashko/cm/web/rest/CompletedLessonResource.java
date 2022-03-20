package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.CompletedLesson;
import com.spiashko.cm.repository.CompletedLessonRepository;
import com.spiashko.cm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.CompletedLesson}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CompletedLessonResource {

    private final Logger log = LoggerFactory.getLogger(CompletedLessonResource.class);

    private static final String ENTITY_NAME = "completedLesson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompletedLessonRepository completedLessonRepository;

    public CompletedLessonResource(CompletedLessonRepository completedLessonRepository) {
        this.completedLessonRepository = completedLessonRepository;
    }

    /**
     * {@code POST  /completed-lessons} : Create a new completedLesson.
     *
     * @param completedLesson the completedLesson to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new completedLesson, or with status {@code 400 (Bad Request)} if the completedLesson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/completed-lessons")
    public ResponseEntity<CompletedLesson> createCompletedLesson(@Valid @RequestBody CompletedLesson completedLesson)
        throws URISyntaxException {
        log.debug("REST request to save CompletedLesson : {}", completedLesson);
        if (completedLesson.getId() != null) {
            throw new BadRequestAlertException("A new completedLesson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CompletedLesson result = completedLessonRepository.save(completedLesson);
        return ResponseEntity
            .created(new URI("/api/completed-lessons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /completed-lessons/:id} : Updates an existing completedLesson.
     *
     * @param id the id of the completedLesson to save.
     * @param completedLesson the completedLesson to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated completedLesson,
     * or with status {@code 400 (Bad Request)} if the completedLesson is not valid,
     * or with status {@code 500 (Internal Server Error)} if the completedLesson couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/completed-lessons/{id}")
    public ResponseEntity<CompletedLesson> updateCompletedLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompletedLesson completedLesson
    ) throws URISyntaxException {
        log.debug("REST request to update CompletedLesson : {}, {}", id, completedLesson);
        if (completedLesson.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, completedLesson.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!completedLessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CompletedLesson result = completedLessonRepository.save(completedLesson);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, completedLesson.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /completed-lessons/:id} : Partial updates given fields of an existing completedLesson, field will ignore if it is null
     *
     * @param id the id of the completedLesson to save.
     * @param completedLesson the completedLesson to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated completedLesson,
     * or with status {@code 400 (Bad Request)} if the completedLesson is not valid,
     * or with status {@code 404 (Not Found)} if the completedLesson is not found,
     * or with status {@code 500 (Internal Server Error)} if the completedLesson couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/completed-lessons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CompletedLesson> partialUpdateCompletedLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompletedLesson completedLesson
    ) throws URISyntaxException {
        log.debug("REST request to partial update CompletedLesson partially : {}, {}", id, completedLesson);
        if (completedLesson.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, completedLesson.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!completedLessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompletedLesson> result = completedLessonRepository
            .findById(completedLesson.getId())
            .map(existingCompletedLesson -> {
                return existingCompletedLesson;
            })
            .map(completedLessonRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, completedLesson.getId().toString())
        );
    }

    /**
     * {@code GET  /completed-lessons} : get all the completedLessons.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of completedLessons in body.
     */
    @GetMapping("/completed-lessons")
    public List<CompletedLesson> getAllCompletedLessons() {
        log.debug("REST request to get all CompletedLessons");
        return completedLessonRepository.findAll();
    }

    /**
     * {@code GET  /completed-lessons/:id} : get the "id" completedLesson.
     *
     * @param id the id of the completedLesson to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the completedLesson, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/completed-lessons/{id}")
    public ResponseEntity<CompletedLesson> getCompletedLesson(@PathVariable Long id) {
        log.debug("REST request to get CompletedLesson : {}", id);
        Optional<CompletedLesson> completedLesson = completedLessonRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(completedLesson);
    }

    /**
     * {@code DELETE  /completed-lessons/:id} : delete the "id" completedLesson.
     *
     * @param id the id of the completedLesson to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/completed-lessons/{id}")
    public ResponseEntity<Void> deleteCompletedLesson(@PathVariable Long id) {
        log.debug("REST request to delete CompletedLesson : {}", id);
        completedLessonRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
