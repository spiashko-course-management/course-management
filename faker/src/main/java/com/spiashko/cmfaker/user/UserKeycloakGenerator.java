package com.spiashko.cmfaker.user;

import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserKeycloakGenerator {

    public static final String ID_PLACEHOLDER = "§ID§";
    public static final String EMAIL_PLACEHOLDER = "§EMAIL§";
    public static final String USERNAME_PLACEHOLDER = "§USERNAME§";
    public static final String FIRST_NAME_PLACEHOLDER = "§FIRST_NAME§";
    public static final String LAST_NAME_PLACEHOLDER = "§LAST_NAME§";
    public static final String CREDENTIALS_ID_PLACEHOLDER = "§CREDENTIALS_ID§";
    public static final String KEYCLOAK_USER_TEMPLATE = "{\"id\":\"§ID§\",\"createdTimestamp\":1505479373742,\"username\":\"§USERNAME§\",\"enabled\":true,\"totp\":false,\"emailVerified\":true,\"firstName\":\"§FIRST_NAME§\",\"lastName\":\"§LAST_NAME§\",\"email\":\"§EMAIL§\",\"credentials\":[{\"id\":\"§CREDENTIALS_ID§\",\"type\":\"password\",\"createdDate\":1505479392766,\"secretData\":\"{\\\"value\\\":\\\"MbKsMgWPnZyImih8s4SaoCSCq+XIY/c6S9F93sXEidHF1TjPWxCqMkec0+o3860CMLXHt3az61cIJOWI0FW9aw==\\\",\\\"salt\\\":\\\"fmpBI1r8R1u75hDLMUlwBw==\\\"}\",\"credentialData\":\"{\\\"hashIterations\\\":27500,\\\"algorithm\\\":\\\"pbkdf2-sha256\\\"}\"}],\"disableableCredentialTypes\":[],\"requiredActions\":[],\"realmRoles\":[\"offline_access\",\"uma_authorization\"],\"clientRoles\":{\"account\":[\"view-profile\",\"manage-account\"]},\"notBefore\":0,\"groups\":[\"/Users\"]}";

    @SneakyThrows
    public void generateKeycloak(List<User> users) {
        File csvOutputFile = new File("user.json");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {

            pw.print("{\"realm\": \"jhipster\", \"users\": [");

            pw.print(
                users.stream()
                    .map(this::getKeycloakUserByTemplate)
                    .collect(Collectors.joining(","))
            );

            pw.print("]}");
        }
    }

    private String getKeycloakUserByTemplate(User user) {
        return KEYCLOAK_USER_TEMPLATE
            .replace(CREDENTIALS_ID_PLACEHOLDER, UUID.randomUUID().toString())
            .replace(ID_PLACEHOLDER, user.id().toString())
            .replace(USERNAME_PLACEHOLDER, user.login())
            .replace(EMAIL_PLACEHOLDER, user.email())
            .replace(FIRST_NAME_PLACEHOLDER, user.first_name())
            .replace(LAST_NAME_PLACEHOLDER, user.last_name());
    }

}
