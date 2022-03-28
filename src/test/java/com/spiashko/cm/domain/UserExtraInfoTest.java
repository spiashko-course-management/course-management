package com.spiashko.cm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.spiashko.cm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserExtraInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserExtraInfo.class);
        UserExtraInfo userExtraInfo1 = new UserExtraInfo();
        userExtraInfo1.setId("id1");
        UserExtraInfo userExtraInfo2 = new UserExtraInfo();
        userExtraInfo2.setId(userExtraInfo1.getId());
        assertThat(userExtraInfo1).isEqualTo(userExtraInfo2);
        userExtraInfo2.setId("id2");
        assertThat(userExtraInfo1).isNotEqualTo(userExtraInfo2);
        userExtraInfo1.setId(null);
        assertThat(userExtraInfo1).isNotEqualTo(userExtraInfo2);
    }
}
