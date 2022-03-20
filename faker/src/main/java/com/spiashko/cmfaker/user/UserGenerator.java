package com.spiashko.cmfaker.user;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserGenerator {

    private final Faker faker = new Faker();

    public static void main(String[] args) {
        UserGenerator generator = new UserGenerator();
        generator.generate();
    }

    @SneakyThrows
    public void generate() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(
                UUID.randomUUID(),
                faker.name().firstName(),
                faker.name().lastName()
            ));
        }

        new UserKeycloakGenerator().generateKeycloak(users);
        new UserCsvGenerator().generateCSV(users);
    }

}
