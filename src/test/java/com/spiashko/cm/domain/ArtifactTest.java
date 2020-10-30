package com.spiashko.cm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.spiashko.cm.web.rest.TestUtil;

public class ArtifactTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Artifact.class);
        Artifact artifact1 = new Artifact();
        artifact1.setId(1L);
        Artifact artifact2 = new Artifact();
        artifact2.setId(artifact1.getId());
        assertThat(artifact1).isEqualTo(artifact2);
        artifact2.setId(2L);
        assertThat(artifact1).isNotEqualTo(artifact2);
        artifact1.setId(null);
        assertThat(artifact1).isNotEqualTo(artifact2);
    }
}
