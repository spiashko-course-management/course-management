package com.spiashko.cm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.spiashko.cm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonExtraInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonExtraInfo.class);
        LessonExtraInfo lessonExtraInfo1 = new LessonExtraInfo();
        lessonExtraInfo1.setId(1L);
        LessonExtraInfo lessonExtraInfo2 = new LessonExtraInfo();
        lessonExtraInfo2.setId(lessonExtraInfo1.getId());
        assertThat(lessonExtraInfo1).isEqualTo(lessonExtraInfo2);
        lessonExtraInfo2.setId(2L);
        assertThat(lessonExtraInfo1).isNotEqualTo(lessonExtraInfo2);
        lessonExtraInfo1.setId(null);
        assertThat(lessonExtraInfo1).isNotEqualTo(lessonExtraInfo2);
    }
}
