package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.LessonExtraInfo;
import com.spiashko.cm.repository.LessonExtraInfoRepository;
import com.spiashko.cm.repository.LessonRepository;
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
 * REST controller for managing {@link com.spiashko.cm.domain.LessonExtraInfo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LessonExtraInfoResource {

    private final Logger log = LoggerFactory.getLogger(LessonExtraInfoResource.class);

    private static final String ENTITY_NAME = "lessonExtraInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LessonExtraInfoRepository lessonExtraInfoRepository;

    private final LessonRepository lessonRepository;

    public LessonExtraInfoResource(LessonExtraInfoRepository lessonExtraInfoRepository, LessonRepository lessonRepository) {
        this.lessonExtraInfoRepository = lessonExtraInfoRepository;
        this.lessonRepository = lessonRepository;
    }

    /**
     * {@code POST  /lesson-extra-infos} : Create a new lessonExtraInfo.
     *
     * @param lessonExtraInfo the lessonExtraInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonExtraInfo, or with status {@code 400 (Bad Request)} if the lessonExtraInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lesson-extra-infos")
    public ResponseEntity<LessonExtraInfo> createLessonExtraInfo(@Valid @RequestBody LessonExtraInfo lessonExtraInfo)
        throws URISyntaxException {
        log.debug("REST request to save LessonExtraInfo : {}", lessonExtraInfo);
        if (lessonExtraInfo.getId() != null) {
            throw new BadRequestAlertException("A new lessonExtraInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(lessonExtraInfo.getLesson())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long lessonId = lessonExtraInfo.getLesson().getId();
        lessonRepository.findById(lessonId).ifPresent(lessonExtraInfo::lesson);
        LessonExtraInfo result = lessonExtraInfoRepository.save(lessonExtraInfo);
        return ResponseEntity
            .created(new URI("/api/lesson-extra-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lesson-extra-infos/:id} : Updates an existing lessonExtraInfo.
     *
     * @param id the id of the lessonExtraInfo to save.
     * @param lessonExtraInfo the lessonExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonExtraInfo,
     * or with status {@code 400 (Bad Request)} if the lessonExtraInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lesson-extra-infos/{id}")
    public ResponseEntity<LessonExtraInfo> updateLessonExtraInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LessonExtraInfo lessonExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to update LessonExtraInfo : {}, {}", id, lessonExtraInfo);
        if (lessonExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LessonExtraInfo result = lessonExtraInfoRepository.save(lessonExtraInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, lessonExtraInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lesson-extra-infos/:id} : Partial updates given fields of an existing lessonExtraInfo, field will ignore if it is null
     *
     * @param id the id of the lessonExtraInfo to save.
     * @param lessonExtraInfo the lessonExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonExtraInfo,
     * or with status {@code 400 (Bad Request)} if the lessonExtraInfo is not valid,
     * or with status {@code 404 (Not Found)} if the lessonExtraInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lesson-extra-infos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LessonExtraInfo> partialUpdateLessonExtraInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LessonExtraInfo lessonExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update LessonExtraInfo partially : {}, {}", id, lessonExtraInfo);
        if (lessonExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonExtraInfo> result = lessonExtraInfoRepository
            .findById(lessonExtraInfo.getId())
            .map(existingLessonExtraInfo -> {
                if (lessonExtraInfo.getContent() != null) {
                    existingLessonExtraInfo.setContent(lessonExtraInfo.getContent());
                }

                return existingLessonExtraInfo;
            })
            .map(lessonExtraInfoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, lessonExtraInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /lesson-extra-infos} : get all the lessonExtraInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lessonExtraInfos in body.
     */
    @GetMapping("/lesson-extra-infos")
    @Transactional(readOnly = true)
    public List<LessonExtraInfo> getAllLessonExtraInfos() {
        log.debug("REST request to get all LessonExtraInfos");
        return lessonExtraInfoRepository.findAll();
    }

    /**
     * {@code GET  /lesson-extra-infos/:id} : get the "id" lessonExtraInfo.
     *
     * @param id the id of the lessonExtraInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonExtraInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lesson-extra-infos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<LessonExtraInfo> getLessonExtraInfo(@PathVariable Long id) {
        log.debug("REST request to get LessonExtraInfo : {}", id);
        Optional<LessonExtraInfo> lessonExtraInfo = lessonExtraInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(lessonExtraInfo);
    }

    /**
     * {@code DELETE  /lesson-extra-infos/:id} : delete the "id" lessonExtraInfo.
     *
     * @param id the id of the lessonExtraInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lesson-extra-infos/{id}")
    public ResponseEntity<Void> deleteLessonExtraInfo(@PathVariable Long id) {
        log.debug("REST request to delete LessonExtraInfo : {}", id);
        lessonExtraInfoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
