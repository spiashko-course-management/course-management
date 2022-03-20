package com.spiashko.cmfaker.enrolment;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class EnrolmentGenerator {

    private final Faker faker = new Faker();

    public static void main(String[] args) {
        List<String> users = List.of(
            "632b0880-c08f-4853-a60c-5f0e08788c78",
            "e9b956c6-2d08-44f1-bbc2-a14effb8fba0",
            "e38e3704-78ca-472f-b237-9e6f8761ae2e",
            "4e23a0cb-1265-4200-8e10-860097aaa0ee",
            "f9f69f16-1acd-4ad5-83d2-e2500c38613c",
            "b133bda8-d99b-4009-a8ea-3e5c89b2cd60",
            "257295cb-173c-40b0-a2d6-9e3aec04aa2a",
            "cfa5c60b-dd45-4bec-a252-ac79634dbcbb"
        );
        new EnrolmentGenerator().generateCSV(users);
    }

    @SneakyThrows
    public void generateCSV(List<String> userIds) {
        File csvOutputFile = new File("enrolment.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("id;course_id;student_id");
            for (int i = 1; i <= 10; i++) {
                int userIndex = faker.random().nextInt(8);
                pw.println(convertToCSV(i, userIds.get(userIndex)));
            }
        }
    }

    private String convertToCSV(int recordIndex, String userId) {
        return String.join(";",
            Integer.toString(recordIndex),
            Integer.toString(recordIndex),
            userId
        );
    }

}
