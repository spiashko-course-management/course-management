package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.LessonDetails;
import com.spiashko.cm.repository.LessonDetailsRepository;
import com.spiashko.cm.repository.LessonRepository;
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
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.LessonDetails}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LessonDetailsResource {

    private final Logger log = LoggerFactory.getLogger(LessonDetailsResource.class);

    private static final String ENTITY_NAME = "lessonDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LessonDetailsRepository lessonDetailsRepository;

    private final LessonRepository lessonRepository;

    public LessonDetailsResource(LessonDetailsRepository lessonDetailsRepository, LessonRepository lessonRepository) {
        this.lessonDetailsRepository = lessonDetailsRepository;
        this.lessonRepository = lessonRepository;
    }

    /**
     * {@code POST  /lesson-details} : Create a new lessonDetails.
     *
     * @param lessonDetails the lessonDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonDetails, or with status {@code 400 (Bad Request)} if the lessonDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lesson-details")
    public ResponseEntity<LessonDetails> createLessonDetails(@Valid @RequestBody LessonDetails lessonDetails) throws URISyntaxException {
        log.debug("REST request to save LessonDetails : {}", lessonDetails);
        if (lessonDetails.getId() != null) {
            throw new BadRequestAlertException("A new lessonDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(lessonDetails.getLesson())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long lessonId = lessonDetails.getLesson().getId();
        lessonRepository.findById(lessonId).ifPresent(lessonDetails::lesson);
        LessonDetails result = lessonDetailsRepository.save(lessonDetails);
        return ResponseEntity.created(new URI("/api/lesson-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lesson-details} : Updates an existing lessonDetails.
     *
     * @param lessonDetails the lessonDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonDetails,
     * or with status {@code 400 (Bad Request)} if the lessonDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lesson-details")
    public ResponseEntity<LessonDetails> updateLessonDetails(@Valid @RequestBody LessonDetails lessonDetails) throws URISyntaxException {
        log.debug("REST request to update LessonDetails : {}", lessonDetails);
        if (lessonDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LessonDetails result = lessonDetailsRepository.save(lessonDetails);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, lessonDetails.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /lesson-details} : get all the lessonDetails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lessonDetails in body.
     */
    @GetMapping("/lesson-details")
    @Transactional(readOnly = true)
    public List<LessonDetails> getAllLessonDetails() {
        log.debug("REST request to get all LessonDetails");
        return lessonDetailsRepository.findAll();
    }

    /**
     * {@code GET  /lesson-details/:id} : get the "id" lessonDetails.
     *
     * @param id the id of the lessonDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lesson-details/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<LessonDetails> getLessonDetails(@PathVariable Long id) {
        log.debug("REST request to get LessonDetails : {}", id);
        Optional<LessonDetails> lessonDetails = lessonDetailsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lessonDetails);
    }

    /**
     * {@code DELETE  /lesson-details/:id} : delete the "id" lessonDetails.
     *
     * @param id the id of the lessonDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lesson-details/{id}")
    public ResponseEntity<Void> deleteLessonDetails(@PathVariable Long id) {
        log.debug("REST request to delete LessonDetails : {}", id);
        lessonDetailsRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
