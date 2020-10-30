package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.UserLogo;
import com.spiashko.cm.repository.UserLogoRepository;
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
 * REST controller for managing {@link com.spiashko.cm.domain.UserLogo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserLogoResource {

    private final Logger log = LoggerFactory.getLogger(UserLogoResource.class);

    private static final String ENTITY_NAME = "userLogo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserLogoRepository userLogoRepository;

    private final LogoRepository logoRepository;

    public UserLogoResource(UserLogoRepository userLogoRepository, LogoRepository logoRepository) {
        this.userLogoRepository = userLogoRepository;
        this.logoRepository = logoRepository;
    }

    /**
     * {@code POST  /user-logos} : Create a new userLogo.
     *
     * @param userLogo the userLogo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userLogo, or with status {@code 400 (Bad Request)} if the userLogo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-logos")
    public ResponseEntity<UserLogo> createUserLogo(@Valid @RequestBody UserLogo userLogo) throws URISyntaxException {
        log.debug("REST request to save UserLogo : {}", userLogo);
        if (userLogo.getId() != null) {
            throw new BadRequestAlertException("A new userLogo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(userLogo.getLogo())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long logoId = userLogo.getLogo().getId();
        logoRepository.findById(logoId).ifPresent(userLogo::logo);
        UserLogo result = userLogoRepository.save(userLogo);
        return ResponseEntity.created(new URI("/api/user-logos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-logos} : Updates an existing userLogo.
     *
     * @param userLogo the userLogo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userLogo,
     * or with status {@code 400 (Bad Request)} if the userLogo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userLogo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-logos")
    public ResponseEntity<UserLogo> updateUserLogo(@Valid @RequestBody UserLogo userLogo) throws URISyntaxException {
        log.debug("REST request to update UserLogo : {}", userLogo);
        if (userLogo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserLogo result = userLogoRepository.save(userLogo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userLogo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-logos} : get all the userLogos.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userLogos in body.
     */
    @GetMapping("/user-logos")
    @Transactional(readOnly = true)
    public List<UserLogo> getAllUserLogos(@RequestParam(required = false) String filter) {
        if ("extendeduser-is-null".equals(filter)) {
            log.debug("REST request to get all UserLogos where extendedUser is null");
            return StreamSupport
                .stream(userLogoRepository.findAll().spliterator(), false)
                .filter(userLogo -> userLogo.getExtendedUser() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all UserLogos");
        return userLogoRepository.findAll();
    }

    /**
     * {@code GET  /user-logos/:id} : get the "id" userLogo.
     *
     * @param id the id of the userLogo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userLogo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-logos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<UserLogo> getUserLogo(@PathVariable Long id) {
        log.debug("REST request to get UserLogo : {}", id);
        Optional<UserLogo> userLogo = userLogoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userLogo);
    }

    /**
     * {@code DELETE  /user-logos/:id} : delete the "id" userLogo.
     *
     * @param id the id of the userLogo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-logos/{id}")
    public ResponseEntity<Void> deleteUserLogo(@PathVariable Long id) {
        log.debug("REST request to delete UserLogo : {}", id);
        userLogoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
