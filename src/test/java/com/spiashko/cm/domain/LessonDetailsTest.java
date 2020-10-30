package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class LessonDetailsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonDetails.class);
        LessonDetails lessonDetails1 = new LessonDetails();
        lessonDetails1.setId(1L);
        LessonDetails lessonDetails2 = new LessonDetails();
        lessonDetails2.setId(lessonDetails1.getId());
        assertThat(lessonDetails1).isEqualTo(lessonDetails2);
        lessonDetails2.setId(2L);
        assertThat(lessonDetails1).isNotEqualTo(lessonDetails2);
        lessonDetails1.setId(null);
        assertThat(lessonDetails1).isNotEqualTo(lessonDetails2);
    }
}
