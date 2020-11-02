package com.spiashko.cm.crudbase.web.swagger;

import lombok.Getter;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static io.github.jhipster.config.JHipsterConstants.SPRING_PROFILE_SWAGGER;

@Profile(SPRING_PROFILE_SWAGGER)
@Component
@Getter
public class OpenApiCustomiserRegistry {

    private final List<OpenApiCustomiser> customisers = new ArrayList<>();

    public void addCustomiser(OpenApiCustomiser customiser) {
        customisers.add(customiser);
    }

}
