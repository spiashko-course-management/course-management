package com.spiashko.cmfaker.course;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class CourseGenerator {

    private final Faker faker = new Faker();

    public static void main(String[] args) {
        List<String> users = List.of(
            "cff72699-21ff-4743-b67d-e3809f2519fa",
            "5b875210-6c55-4460-86e7-b092dd6c0e6e"
        );
        new CourseGenerator().generateCSV(users);
    }

    @SneakyThrows
    public void generateCSV(List<String> userIds) {
        File csvOutputFile = new File("course.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("id;title;teacher_id");
            for (int i = 1; i <= 10; i++) {
                int userIndex = faker.random().nextInt(2);
                pw.println(convertToCSV(i, userIds.get(userIndex)));
            }
        }
    }

    private String convertToCSV(int recordIndex, String userId) {
        return String.join(";",
            Integer.toString(recordIndex),
            faker.book().title(),
            userId
        );
    }

}
