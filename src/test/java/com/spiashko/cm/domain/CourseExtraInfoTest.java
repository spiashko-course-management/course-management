package com.spiashko.cm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.spiashko.cm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseExtraInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseExtraInfo.class);
        CourseExtraInfo courseExtraInfo1 = new CourseExtraInfo();
        courseExtraInfo1.setId(1L);
        CourseExtraInfo courseExtraInfo2 = new CourseExtraInfo();
        courseExtraInfo2.setId(courseExtraInfo1.getId());
        assertThat(courseExtraInfo1).isEqualTo(courseExtraInfo2);
        courseExtraInfo2.setId(2L);
        assertThat(courseExtraInfo1).isNotEqualTo(courseExtraInfo2);
        courseExtraInfo1.setId(null);
        assertThat(courseExtraInfo1).isNotEqualTo(courseExtraInfo2);
    }
}
