package com.spiashko.cm;

import com.spiashko.cm.config.TestSecurityConfiguration;
import com.spiashko.cm.security.AuthoritiesConstants;
import com.spiashko.cm.web.rest.UserResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest(classes = {CourseManagementApp.class, TestSecurityConfiguration.class})
public class RegressionIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @Transactional
    public void runRegression() throws Exception {

        // create teacher by get account resource
        // login as created teacher
        // teacher creates full course
        // create student by get account resource
        // login as created student
        // student get list of courses
        // buy first course
        // retrieve bought course
        // complete the first lesson
    }
}
