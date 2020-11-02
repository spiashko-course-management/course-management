package com.spiashko.cm.crudbase.web.swagger;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.ReturnTypeParser;
import org.springdoc.core.SpringDocAnnotationsUtils;
import org.springdoc.core.converters.ConverterUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static io.github.jhipster.config.JHipsterConstants.SPRING_PROFILE_SWAGGER;

@Profile(SPRING_PROFILE_SWAGGER)
@Slf4j
@RequiredArgsConstructor
@Component
public class JsonIgnorePropertiesWorkAroundOperationCustomizer implements OperationCustomizer {

    private final OpenApiCustomiserRegistry registry;
    private final List<ReturnTypeParser> returnTypeParsers;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {

        MethodParameter reqBodyParam = Arrays.stream(handlerMethod.getMethodParameters())
            .filter(methodParameter -> methodParameter.hasParameterAnnotation(RequestBody.class))
            .findFirst()
            .orElse(null);

        if (reqBodyParam != null) {
            JsonView jsonView = reqBodyParam.getParameterAnnotation(JsonView.class);
            Type clazz = getEntityClass(reqBodyParam);
            addCustomiser(clazz, jsonView, reqBodyParam);
        }

        MethodParameter returnParam = handlerMethod.getReturnType();

        Type returnType = getEntityClass(returnParam);
        JsonView jsonView = handlerMethod.getMethodAnnotation(JsonView.class);
        addCustomiser(returnType, jsonView, returnParam);

        return operation;
    }

    private void addCustomiser(Type clazz, JsonView jsonView, MethodParameter methodParameter) {
        if (Void.class.equals(clazz)) {
            return;
        }
        if (Object.class.equals(clazz) ||
            String.class.equals(clazz)) {
            log.info("object resolution result will be ignored for method parameter " + methodParameter);
            return;
        }
        registry.addCustomiser(
            (OpenAPI openApi) -> {
                try {
                    Schema<?> schema = SpringDocAnnotationsUtils.extractSchema(openApi.getComponents(), clazz, jsonView);
                    String schemaRef = schema.get$ref();
                    String schemaName = schemaRef.substring(schemaRef.lastIndexOf('/') + 1);
                    //regenerate schema to leverage JsonIgnoreProperties issue
                    openApi.getComponents().getSchemas().remove(schemaName);
                    SpringDocAnnotationsUtils.extractSchema(openApi.getComponents(), clazz, jsonView);
                } catch (Exception ex) {
                    log.error("failed to apply workaround for class " + clazz.getTypeName(), ex);
                }
            }
        );
    }

    private Type getEntityClass(MethodParameter methodParameter) {
        Type returnType = Object.class;
        for (ReturnTypeParser returnTypeParser : returnTypeParsers) {
            if (returnType.getTypeName().equals(Object.class.getTypeName())) {
                returnType = returnTypeParser.getReturnType(methodParameter);
            } else
                break;
        }

        returnType = unwrap(returnType);
        return returnType;
    }

    private Type unwrap(Type returnType) {
        if (returnType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();

            if (types != null) {
                Class<?> rawClass = ResolvableType.forType(returnType).getRawClass();
                if (ConverterUtils.isResponseTypeWrapper(rawClass) ||
                    Iterable.class.isAssignableFrom(rawClass)) {
                    return unwrap(types[0]);
                }
            }
        }
        return returnType;
    }
}
