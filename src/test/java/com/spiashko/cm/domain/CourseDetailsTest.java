package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class CourseDetailsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseDetails.class);
        CourseDetails courseDetails1 = new CourseDetails();
        courseDetails1.setId(1L);
        CourseDetails courseDetails2 = new CourseDetails();
        courseDetails2.setId(courseDetails1.getId());
        assertThat(courseDetails1).isEqualTo(courseDetails2);
        courseDetails2.setId(2L);
        assertThat(courseDetails1).isNotEqualTo(courseDetails2);
        courseDetails1.setId(null);
        assertThat(courseDetails1).isNotEqualTo(courseDetails2);
    }
}
