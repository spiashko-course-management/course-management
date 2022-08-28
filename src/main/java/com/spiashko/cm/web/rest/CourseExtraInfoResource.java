package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.CourseExtraInfo;
import com.spiashko.cm.repository.CourseExtraInfoRepository;
import com.spiashko.cm.repository.CourseRepository;
import com.spiashko.cm.utils.FetchUtils;
import com.spiashko.cm.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import static com.spiashko.cm.utils.FetchUtils.*;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.CourseExtraInfo}.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Transactional
public class CourseExtraInfoResource {

    private static final String ENTITY_NAME = "courseExtraInfo";
    private final Logger log = LoggerFactory.getLogger(CourseExtraInfoResource.class);
    private final CourseExtraInfoRepository courseExtraInfoRepository;
    private final FetchUtils fetchUtils;
    private final CourseRepository courseRepository;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    /**
     * {@code POST  /course-extra-infos} : Create a new courseExtraInfo.
     *
     * @param courseExtraInfo the courseExtraInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseExtraInfo, or with status {@code 400 (Bad Request)} if the courseExtraInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/course-extra-infos")
    public ResponseEntity<CourseExtraInfo> createCourseExtraInfo(@Valid @RequestBody CourseExtraInfo courseExtraInfo)
        throws URISyntaxException {
        log.debug("REST request to save CourseExtraInfo : {}", courseExtraInfo);
        if (courseExtraInfo.getId() != null) {
            throw new BadRequestAlertException("A new courseExtraInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(courseExtraInfo.getCourse())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long courseId = courseExtraInfo.getCourse().getId();
        courseRepository.findById(courseId).ifPresent(courseExtraInfo::course);
        CourseExtraInfo result = courseExtraInfoRepository.save(courseExtraInfo);
        return ResponseEntity
            .created(new URI("/api/course-extra-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /course-extra-infos/:id} : Updates an existing courseExtraInfo.
     *
     * @param id              the id of the courseExtraInfo to save.
     * @param courseExtraInfo the courseExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseExtraInfo,
     * or with status {@code 400 (Bad Request)} if the courseExtraInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/course-extra-infos/{id}")
    public ResponseEntity<CourseExtraInfo> updateCourseExtraInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseExtraInfo courseExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to update CourseExtraInfo : {}, {}", id, courseExtraInfo);
        if (courseExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CourseExtraInfo result = courseExtraInfoRepository.save(courseExtraInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, courseExtraInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /course-extra-infos/:id} : Partial updates given fields of an existing courseExtraInfo, field will ignore if it is null
     *
     * @param id              the id of the courseExtraInfo to save.
     * @param courseExtraInfo the courseExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseExtraInfo,
     * or with status {@code 400 (Bad Request)} if the courseExtraInfo is not valid,
     * or with status {@code 404 (Not Found)} if the courseExtraInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/course-extra-infos/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<CourseExtraInfo> partialUpdateCourseExtraInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseExtraInfo courseExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update CourseExtraInfo partially : {}, {}", id, courseExtraInfo);
        if (courseExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CourseExtraInfo> result = courseExtraInfoRepository
            .findById(courseExtraInfo.getId())
            .map(existingCourseExtraInfo -> {
                if (courseExtraInfo.getSummary() != null) {
                    existingCourseExtraInfo.setSummary(courseExtraInfo.getSummary());
                }

                return existingCourseExtraInfo;
            })
            .map(courseExtraInfoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, courseExtraInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /course-extra-infos} : get all the courseExtraInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseExtraInfos in body.
     */
    @GetMapping("/course-extra-infos")
    @Transactional(readOnly = true)
    public List<CourseExtraInfo> getAllCourseExtraInfos() {
        log.debug("REST request to get all CourseExtraInfos");
        return courseExtraInfoRepository.findAll();
    }

    /**
     * {@code GET  /course-extra-infos/:id} : get the "id" courseExtraInfo.
     *
     * @param id the id of the courseExtraInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseExtraInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/course-extra-infos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CourseExtraInfo> getCourseExtraInfo(@PathVariable Long id, @Valid IncludeRequest request) {
        log.debug("REST request to get CourseExtraInfo : {}", id);
        Optional<CourseExtraInfo> courseExtraInfo = fetchUtils.fetchById(courseExtraInfoRepository, id, request, CourseExtraInfo.class);
        return ResponseUtil.wrapOrNotFound(courseExtraInfo);
    }

    /**
     * {@code DELETE  /course-extra-infos/:id} : delete the "id" courseExtraInfo.
     *
     * @param id the id of the courseExtraInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/course-extra-infos/{id}")
    public ResponseEntity<Void> deleteCourseExtraInfo(@PathVariable Long id) {
        log.debug("REST request to delete CourseExtraInfo : {}", id);
        courseExtraInfoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
