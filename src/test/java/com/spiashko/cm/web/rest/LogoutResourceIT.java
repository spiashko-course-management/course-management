package com.spiashko.cm.web.rest;

import com.spiashko.cm.IntegrationTest;
import com.spiashko.cm.security.AuthoritiesConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.spiashko.cm.test.util.OAuth2TestUtil.authenticationToken;
import static com.spiashko.cm.test.util.OAuth2TestUtil.registerAuthenticationToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LogoutResource} REST controller.
 */
@IntegrationTest
class LogoutResourceIT {

    @Autowired
    private ClientRegistrationRepository registrations;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ClientRegistration clientRegistration;

    private MockMvc restLogoutMockMvc;

    private Map<String, Object> claims;

    @BeforeEach
    public void before() throws Exception {
        claims = new HashMap<>();
        claims.put("groups", Collections.singletonList(AuthoritiesConstants.USER));
        claims.put("sub", 123);

        SecurityContextHolder
            .getContext()
            .setAuthentication(registerAuthenticationToken(authorizedClientService, clientRegistration, authenticationToken(claims)));
        SecurityContextHolderAwareRequestFilter authInjector = new SecurityContextHolderAwareRequestFilter();
        authInjector.afterPropertiesSet();

        this.restLogoutMockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void getLogoutInformation() throws Exception {
        final String redirectUrl = "http://localhost:8080";
        String logoutUrl =
            this.registrations.findByRegistrationId("oidc")
                .getProviderDetails()
                .getConfigurationMetadata()
                .get("end_session_endpoint")
                .toString();
        logoutUrl = logoutUrl + "?redirect_uri=" + redirectUrl;
        restLogoutMockMvc
            .perform(get("http://localhost:8080/logout?redirectUrl=" + redirectUrl))
            .andExpect(status().isFound())
            .andExpect(header().stringValues(HttpHeaders.LOCATION, logoutUrl));
    }
}
