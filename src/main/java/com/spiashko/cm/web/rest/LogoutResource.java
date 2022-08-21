package com.spiashko.cm.web.rest;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * REST controller for managing global OIDC logout.
 */
@Controller
public class LogoutResource {

    private final ClientRegistration registration;

    public LogoutResource(ClientRegistrationRepository registrations) {
        this.registration = registrations.findByRegistrationId("oidc");
    }

    /**
     * {@code GET  /api/logout} : logout the current user.
     *
     * @param request the {@link HttpServletRequest}.
     * @return the redirect with a global logout URL.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, @RequestParam String redirectUrl) {
        String endSessionEndpoint = this.registration
            .getProviderDetails()
            .getConfigurationMetadata()
            .get("end_session_endpoint").toString();

        String logoutUrl = endSessionEndpoint + "?redirect_uri=" + redirectUrl;

        request.getSession().invalidate();
        return "redirect:" + logoutUrl;
    }
}
