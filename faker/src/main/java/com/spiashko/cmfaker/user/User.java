package com.spiashko.cmfaker.user;

import java.util.UUID;

public record User(
    UUID id,
    String first_name,
    String last_name
) {

    public String login() {
        return (first_name + "_" + last_name).toLowerCase();
    }

    public String email() {
        return login() + "@localhost";
    }
}
