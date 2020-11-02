package com.spiashko.cm.crudbase.web.restspec;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RestSpec {

    String includeParamName() default "include";

    String filterParamName() default "filter";

    AndPathVarEq[] extensionFromPath() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @interface AndPathVarEq {

        String pathVar();

        String attributePath();

    }

}
