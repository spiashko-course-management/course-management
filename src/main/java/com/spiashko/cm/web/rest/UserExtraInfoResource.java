package com.spiashko.cm.web.rest;

import com.spiashko.cm.domain.CourseExtraInfo;
import com.spiashko.cm.domain.UserExtraInfo;
import com.spiashko.cm.domain.UserExtraInfo_;
import com.spiashko.cm.repository.UserExtraInfoRepository;
import com.spiashko.cm.repository.UserRepository;
import com.spiashko.cm.utils.FetchUtils;
import com.spiashko.cm.utils.FetchUtils.IncludeRequest;
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

/**
 * REST controller for managing {@link com.spiashko.cm.domain.UserExtraInfo}.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Transactional
public class UserExtraInfoResource {

    private static final String ENTITY_NAME = "userExtraInfo";
    private final Logger log = LoggerFactory.getLogger(UserExtraInfoResource.class);
    private final UserExtraInfoRepository userExtraInfoRepository;
    private final FetchUtils fetchUtils;
    private final UserRepository userRepository;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    /**
     * {@code POST  /user-extra-infos} : Create a new userExtraInfo.
     *
     * @param userExtraInfo the userExtraInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userExtraInfo, or with status {@code 400 (Bad Request)} if the userExtraInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-extra-infos")
    public ResponseEntity<UserExtraInfo> createUserExtraInfo(@Valid @RequestBody UserExtraInfo userExtraInfo) throws URISyntaxException {
        log.debug("REST request to save UserExtraInfo : {}", userExtraInfo);
        if (userExtraInfo.getId() != null) {
            throw new BadRequestAlertException("A new userExtraInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(userExtraInfo.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        String userId = userExtraInfo.getUser().getId();
        userRepository.findById(userId).ifPresent(userExtraInfo::user);
        UserExtraInfo result = userExtraInfoRepository.save(userExtraInfo);
        return ResponseEntity
            .created(new URI("/api/user-extra-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /user-extra-infos/:id} : Updates an existing userExtraInfo.
     *
     * @param id            the id of the userExtraInfo to save.
     * @param userExtraInfo the userExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtraInfo,
     * or with status {@code 400 (Bad Request)} if the userExtraInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-extra-infos/{id}")
    public ResponseEntity<UserExtraInfo> updateUserExtraInfo(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody UserExtraInfo userExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to update UserExtraInfo : {}, {}", id, userExtraInfo);
        if (userExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserExtraInfo result = userExtraInfoRepository.save(userExtraInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userExtraInfo.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-extra-infos/:id} : Partial updates given fields of an existing userExtraInfo, field will ignore if it is null
     *
     * @param id            the id of the userExtraInfo to save.
     * @param userExtraInfo the userExtraInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userExtraInfo,
     * or with status {@code 400 (Bad Request)} if the userExtraInfo is not valid,
     * or with status {@code 404 (Not Found)} if the userExtraInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the userExtraInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-extra-infos/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<UserExtraInfo> partialUpdateUserExtraInfo(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody UserExtraInfo userExtraInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserExtraInfo partially : {}, {}", id, userExtraInfo);
        if (userExtraInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userExtraInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userExtraInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserExtraInfo> result = userExtraInfoRepository
            .findById(userExtraInfo.getId())
            .map(existingUserExtraInfo -> {
                if (userExtraInfo.getBio() != null) {
                    existingUserExtraInfo.setBio(userExtraInfo.getBio());
                }

                return existingUserExtraInfo;
            })
            .map(userExtraInfoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userExtraInfo.getId())
        );
    }

    /**
     * {@code GET  /user-extra-infos} : get all the userExtraInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userExtraInfos in body.
     */
    @GetMapping("/user-extra-infos")
    @Transactional(readOnly = true)
    public List<UserExtraInfo> getAllUserExtraInfos() {
        log.debug("REST request to get all UserExtraInfos");
        return userExtraInfoRepository.findAll();
    }

    /**
     * {@code GET  /user-extra-infos/:id} : get the "id" userExtraInfo.
     *
     * @param id the id of the userExtraInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userExtraInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-extra-infos/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<UserExtraInfo> getUserExtraInfo(@PathVariable String id, @Valid IncludeRequest request) {
        log.debug("REST request to get UserExtraInfo : {}", id);
        Optional<UserExtraInfo> userExtraInfo = fetchUtils.fetchById(
            userExtraInfoRepository, UserExtraInfo_.id, id, request, UserExtraInfo.class);
        return ResponseUtil.wrapOrNotFound(userExtraInfo);
    }

    /**
     * {@code DELETE  /user-extra-infos/:id} : delete the "id" userExtraInfo.
     *
     * @param id the id of the userExtraInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-extra-infos/{id}")
    public ResponseEntity<Void> deleteUserExtraInfo(@PathVariable String id) {
        log.debug("REST request to delete UserExtraInfo : {}", id);
        userExtraInfoRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
