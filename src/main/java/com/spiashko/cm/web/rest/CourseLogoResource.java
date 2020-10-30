package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.CourseLogo;
import com.spiashko.cm.repository.CourseLogoRepository;
import com.spiashko.cm.repository.LogoRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.CourseLogo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CourseLogoResource {

    private final Logger log = LoggerFactory.getLogger(CourseLogoResource.class);

    private static final String ENTITY_NAME = "courseLogo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseLogoRepository courseLogoRepository;

    private final LogoRepository logoRepository;

    public CourseLogoResource(CourseLogoRepository courseLogoRepository, LogoRepository logoRepository) {
        this.courseLogoRepository = courseLogoRepository;
        this.logoRepository = logoRepository;
    }

    /**
     * {@code POST  /course-logos} : Create a new courseLogo.
     *
     * @param courseLogo the courseLogo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseLogo, or with status {@code 400 (Bad Request)} if the courseLogo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/course-logos")
    public ResponseEntity<CourseLogo> createCourseLogo(@Valid @RequestBody CourseLogo courseLogo) throws URISyntaxException {
        log.debug("REST request to save CourseLogo : {}", courseLogo);
        if (courseLogo.getId() != null) {
            throw new BadRequestAlertException("A new courseLogo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(courseLogo.getLogo())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long logoId = courseLogo.getLogo().getId();
        logoRepository.findById(logoId).ifPresent(courseLogo::logo);
        CourseLogo result = courseLogoRepository.save(courseLogo);
        return ResponseEntity.created(new URI("/api/course-logos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /course-logos} : Updates an existing courseLogo.
     *
     * @param courseLogo the courseLogo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseLogo,
     * or with status {@code 400 (Bad Request)} if the courseLogo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the courseLogo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/course-logos")
    public ResponseEntity<CourseLogo> updateCourseLogo(@Valid @RequestBody CourseLogo courseLogo) throws URISyntaxException {
        log.debug("REST request to update CourseLogo : {}", courseLogo);
        if (courseLogo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CourseLogo result = courseLogoRepository.save(courseLogo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, courseLogo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /course-logos} : get all the courseLogos.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courseLogos in body.
     */
    @GetMapping("/course-logos")
    @Transactional(readOnly = true)
    public List<CourseLogo> getAllCourseLogos(@RequestParam(required = false) String filter) {
        if ("course-is-null".equals(filter)) {
            log.debug("REST request to get all CourseLogos where course is null");
            return StreamSupport
                .stream(courseLogoRepository.findAll().spliterator(), false)
                .filter(courseLogo -> courseLogo.getCourse() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all CourseLogos");
        return courseLogoRepository.findAll();
    }

    /**
     * {@code GET  /course-logos/:id} : get the "id" courseLogo.
     *
     * @param id the id of the courseLogo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseLogo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/course-logos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CourseLogo> getCourseLogo(@PathVariable Long id) {
        log.debug("REST request to get CourseLogo : {}", id);
        Optional<CourseLogo> courseLogo = courseLogoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(courseLogo);
    }

    /**
     * {@code DELETE  /course-logos/:id} : delete the "id" courseLogo.
     *
     * @param id the id of the courseLogo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/course-logos/{id}")
    public ResponseEntity<Void> deleteCourseLogo(@PathVariable Long id) {
        log.debug("REST request to delete CourseLogo : {}", id);
        courseLogoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
