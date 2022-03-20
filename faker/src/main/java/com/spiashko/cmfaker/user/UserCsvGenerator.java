package com.spiashko.cmfaker.user;

import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class UserCsvGenerator {

    @SneakyThrows
    public void generateCSV(List<User> users) {
        File csvOutputFile = new File("user.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("id;login;first_name;last_name;email;image_url;activated;lang_key;created_by;created_date;last_modified_by;last_modified_date");
            users.stream()
                .map(this::convertToCSV)
                .forEach(pw::println);
        }
    }

    private String convertToCSV(User user) {
        return String.join(";",
            user.id().toString(),
            user.login(),
            user.first_name(),
            user.last_name(),
            user.email(),
            "",
            "true",
            "en",
            user.login(),
            "2022-03-20 13:59:09.857947",
            user.login(),
            "2022-03-20 13:59:09.857947"
        );
    }

}
