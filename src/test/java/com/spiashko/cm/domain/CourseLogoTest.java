package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class CourseLogoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseLogo.class);
        CourseLogo courseLogo1 = new CourseLogo();
        courseLogo1.setId(1L);
        CourseLogo courseLogo2 = new CourseLogo();
        courseLogo2.setId(courseLogo1.getId());
        assertThat(courseLogo1).isEqualTo(courseLogo2);
        courseLogo2.setId(2L);
        assertThat(courseLogo1).isNotEqualTo(courseLogo2);
        courseLogo1.setId(null);
        assertThat(courseLogo1).isNotEqualTo(courseLogo2);
    }
}
