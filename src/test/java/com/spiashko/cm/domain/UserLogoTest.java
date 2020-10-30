package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class UserLogoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserLogo.class);
        UserLogo userLogo1 = new UserLogo();
        userLogo1.setId(1L);
        UserLogo userLogo2 = new UserLogo();
        userLogo2.setId(userLogo1.getId());
        assertThat(userLogo1).isEqualTo(userLogo2);
        userLogo2.setId(2L);
        assertThat(userLogo1).isNotEqualTo(userLogo2);
        userLogo1.setId(null);
        assertThat(userLogo1).isNotEqualTo(userLogo2);
    }
}
