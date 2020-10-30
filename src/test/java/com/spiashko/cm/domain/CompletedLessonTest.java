package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class CompletedLessonTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompletedLesson.class);
        CompletedLesson completedLesson1 = new CompletedLesson();
        completedLesson1.setId(1L);
        CompletedLesson completedLesson2 = new CompletedLesson();
        completedLesson2.setId(completedLesson1.getId());
        assertThat(completedLesson1).isEqualTo(completedLesson2);
        completedLesson2.setId(2L);
        assertThat(completedLesson1).isNotEqualTo(completedLesson2);
        completedLesson1.setId(null);
        assertThat(completedLesson1).isNotEqualTo(completedLesson2);
    }
}
