package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.Logo;
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
import java.util.Optional;

/**
 * REST controller for managing {@link com.spiashko.cm.domain.Logo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LogoResource {

    private final Logger log = LoggerFactory.getLogger(LogoResource.class);

    private static final String ENTITY_NAME = "logo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LogoRepository logoRepository;

    public LogoResource(LogoRepository logoRepository) {
        this.logoRepository = logoRepository;
    }

    /**
     * {@code POST  /logos} : Create a new logo.
     *
     * @param logo the logo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new logo, or with status {@code 400 (Bad Request)} if the logo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/logos")
    public ResponseEntity<Logo> createLogo(@Valid @RequestBody Logo logo) throws URISyntaxException {
        log.debug("REST request to save Logo : {}", logo);
        if (logo.getId() != null) {
            throw new BadRequestAlertException("A new logo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Logo result = logoRepository.save(logo);
        return ResponseEntity.created(new URI("/api/logos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /logos} : Updates an existing logo.
     *
     * @param logo the logo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logo,
     * or with status {@code 400 (Bad Request)} if the logo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the logo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/logos")
    public ResponseEntity<Logo> updateLogo(@Valid @RequestBody Logo logo) throws URISyntaxException {
        log.debug("REST request to update Logo : {}", logo);
        if (logo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Logo result = logoRepository.save(logo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, logo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /logos} : get all the logos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logos in body.
     */
    @GetMapping("/logos")
    public List<Logo> getAllLogos() {
        log.debug("REST request to get all Logos");
        return logoRepository.findAll();
    }

    /**
     * {@code GET  /logos/:id} : get the "id" logo.
     *
     * @param id the id of the logo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the logo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/logos/{id}")
    public ResponseEntity<Logo> getLogo(@PathVariable Long id) {
        log.debug("REST request to get Logo : {}", id);
        Optional<Logo> logo = logoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(logo);
    }

    /**
     * {@code DELETE  /logos/:id} : delete the "id" logo.
     *
     * @param id the id of the logo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/logos/{id}")
    public ResponseEntity<Void> deleteLogo(@PathVariable Long id) {
        log.debug("REST request to delete Logo : {}", id);
        logoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
