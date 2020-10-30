package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.CourseDetails;
import com.spiashko.cm.repository.CourseDetailsRepository;
import com.spiashko.cm.repository.CourseRepository;
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
 * REST controller for managing {@link com.spiashko.cm.domain.CourseDetails}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CourseDetailsResource {

    private final Logger log = LoggerFactory.getLogger(CourseDetailsResource.class);

    private static final String ENTITY_NAME = "courseDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseDetailsRepository courseDetailsRepository;

    private final CourseRepository courseRepository;

    public CourseDetailsResource(CourseDetailsRepository courseDetailsRepository, CourseRepository courseRepository) {
        this.courseDetailsRepository = courseDetailsRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * {@code POST  /course-details} : Create a new courseDetails.
     *
     * @param courseDetails the courseDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseDetails, or with status {@code 400 (Bad Request)} if the courseDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/course-details")
    public ResponseEntity<CourseDetails> createCourseDetails(@Valid @RequestBody CourseDetails courseDetails) throws URISyntaxException {
        log.debug("REST request to save CourseDetails : {}", courseDetails);
        if (courseDetails.getId() != null) {
            throw new BadRequestAlertException("A new courseDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(courseDetails.getCourse())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long courseId = courseDetails.getCourse().getId();
        courseRepository.findById(courseId).ifPresent(courseDetails::course);
        CourseDetails result = courseDetailsRepository.save(courseDetails);
        return ResponseEntity.created(new URI("/api/course-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /course-details} : Updates an existing courseDetails.
     *
     * @param courseDetails the courseDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseDetails,
     * or with status {@code 400 (Bad Request)} if the courseDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/course-details")
    public ResponseEntity<CourseDetails> updateCourseDetails(@Valid @RequestBody CourseDetails courseDetails) throws URISyntaxException {
        log.debug("REST request to update CourseDetails : {}", courseDetails);
        if (courseDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CourseDetails result = courseDetailsRepository.save(courseDetails);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, courseDetails.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /course-details} : get all the courseDetails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseDetails in body.
     */
    @GetMapping("/course-details")
    @Transactional(readOnly = true)
    public List<CourseDetails> getAllCourseDetails() {
        log.debug("REST request to get all CourseDetails");
        return courseDetailsRepository.findAll();
    }

    /**
     * {@code GET  /course-details/:id} : get the "id" courseDetails.
     *
     * @param id the id of the courseDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/course-details/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CourseDetails> getCourseDetails(@PathVariable Long id) {
        log.debug("REST request to get CourseDetails : {}", id);
        Optional<CourseDetails> courseDetails = courseDetailsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(courseDetails);
    }

    /**
     * {@code DELETE  /course-details/:id} : delete the "id" courseDetails.
     *
     * @param id the id of the courseDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/course-details/{id}")
    public ResponseEntity<Void> deleteCourseDetails(@PathVariable Long id) {
        log.debug("REST request to delete CourseDetails : {}", id);
        courseDetailsRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
