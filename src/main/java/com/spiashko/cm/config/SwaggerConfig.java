package com.spiashko.cm.config;

import com.spiashko.cm.crudbase.web.swagger.OpenApiCustomiserRegistry;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import static io.github.jhipster.config.JHipsterConstants.SPRING_PROFILE_SWAGGER;

@Profile(SPRING_PROFILE_SWAGGER)
@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Specification.class);
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthenticationPrincipal.class);
    }

    @Bean
    public OpenApiCustomiser myOpenApiCustomiser(OpenApiCustomiserRegistry openApiCustomiserRegistry) {
        return (OpenAPI openApi) ->
            openApiCustomiserRegistry.getCustomisers().forEach(customiser -> customiser.customise(openApi));
    }

}
