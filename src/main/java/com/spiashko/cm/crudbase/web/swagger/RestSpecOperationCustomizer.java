package com.spiashko.cm.crudbase.web.swagger;

import com.spiashko.cm.crudbase.web.restspec.RestSpec;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.SpringDocAnnotationsUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.jhipster.config.JHipsterConstants.SPRING_PROFILE_SWAGGER;

@Profile(SPRING_PROFILE_SWAGGER)
@RequiredArgsConstructor
@Component
public class RestSpecOperationCustomizer implements OperationCustomizer {

    private final OpenApiCustomiserRegistry registry;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {

        List<MethodParameter> specParams = Arrays.stream(handlerMethod.getMethodParameters())
            .filter(methodParameter -> Specification.class.equals(methodParameter.getParameterType()) &&
                methodParameter.hasParameterAnnotation(RestSpec.class))
            .collect(Collectors.toList());

        if (specParams.isEmpty()) {
            return operation;
        }

        specParams.forEach((param) -> enrichOperationWithSpecParam(operation, param));

        return operation;
    }

    private void enrichOperationWithSpecParam(Operation operation, MethodParameter parameter) {

        RestSpec restSpecAnnotation = Objects.requireNonNull(parameter.getParameterAnnotation(RestSpec.class));

        Parameter parameterInclude = new Parameter()
            .in(ParameterIn.QUERY.toString())
            .required(false)
            .name(restSpecAnnotation.includeParamName())
            .schema(new StringSchema());
        Parameter parameterFilter = new Parameter()
            .in(ParameterIn.QUERY.toString())
            .required(false)
            .name(restSpecAnnotation.filterParamName())
            .schema(new StringSchema());

        Class<?> clazz = getEntityClass(parameter);

        registry.addCustomiser(
            (OpenAPI openApi) -> {
                Schema<?> schema = SpringDocAnnotationsUtils.extractSchema(openApi.getComponents(), clazz, null);
                String schemaRef = schema.get$ref();
                String schemaName = schemaRef.substring(schemaRef.lastIndexOf('/') + 1);

                //regenerate schema to leverage JsonIgnoreProperties issue
                openApi.getComponents().getSchemas().remove(schemaName);
                SpringDocAnnotationsUtils.extractSchema(openApi.getComponents(), clazz, null);

                parameterInclude.addExtension("x-included-object-schema", schemaRef);
                parameterInclude.setDescription("Fetch criteria in format <property_name_1>;<property_name_2>. " +
                    "Property names based on `" + schemaName + "` schema. " +
                    "Symbol '.'(dot) should be used for nested properties.");
                parameterFilter.addExtension("x-filtered-object-schema", schemaRef);
                parameterFilter.setDescription("Filter criteria in RSQL/FIQL format. " +
                    "Property names based on `" + schemaName + "` schema. " +
                    "Symbol '.'(dot) should be used for nested properties.");
            }
        );

        operation.addParametersItem(parameterInclude);
    }

    private Class<?> getEntityClass(MethodParameter parameter) {
        return (Class<?>) ((ParameterizedType) parameter.getGenericParameterType()).getActualTypeArguments()[0];
    }

}
