package com.spiashko.cm.crudbase.web.restspec;

import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class RestSpecArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();

        return paramType.isInterface() &&
            Specification.class.isAssignableFrom(paramType) &&
            isAnnotated(parameter);
    }

    private boolean isAnnotated(MethodParameter methodParameter) {
        for (Annotation annotation : methodParameter.getParameterAnnotations()) {
            if (RestSpec.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        RestSpec restSpecAnnotation = parameter.getParameterAnnotation(RestSpec.class);

        Specification<Object> includeSpec = getIncludeSpecification(restSpecAnnotation, webRequest);
        Specification<Object> filterSpec = getFilterSpecification(restSpecAnnotation, webRequest);

        return Specification.where(includeSpec).and(filterSpec);
    }

    private Specification<Object> getIncludeSpecification(RestSpec restSpecAnnotation, NativeWebRequest webRequest) {
        String includeParamName = Objects.requireNonNull(restSpecAnnotation).includeParamName();
        String value = webRequest.getParameter(includeParamName);

        Specification<Object> includeSpec = null;
        if (StringUtils.isNotBlank(value)) {
            includeSpec = Arrays.stream(value.split(";"))
                .reduce(Specification.where(null),
                    (objectSpecification, s) -> objectSpecification.and(buildIncludeSpec(s)),
                    Specification::and);
        }
        return includeSpec;
    }

    private Specification<Object> getFilterSpecification(RestSpec restSpecAnnotation, NativeWebRequest webRequest) {
        String paramName = Objects.requireNonNull(restSpecAnnotation).filterParamName();
        RestSpec.AndPathVarEq[] andPathVarEqs = restSpecAnnotation.extensionFromPath();

        String rsqlString = webRequest.getParameter(paramName);
        Specification<Object> rsqlSpec = RSQLJPASupport.toSpecification(rsqlString);

        if (andPathVarEqs.length == 0) {
            return rsqlSpec;
        }

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String> uriTemplateVariables =
            (Map<String, String>) Objects.requireNonNull(httpServletRequest)
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Specification<Object> fullSpec = rsqlSpec;
        for (RestSpec.AndPathVarEq andPathVarEq : andPathVarEqs) {
            String attributePath = andPathVarEq.attributePath();
            String pathVar = andPathVarEq.pathVar();

            String pathVarValue = uriTemplateVariables.get(pathVar);
            fullSpec = buildEqSpec(pathVarValue, attributePath).and(fullSpec);
        }

        return fullSpec;
    }

    private Specification<Object> buildIncludeSpec(String attributePath) {
        return (Specification<Object>) (root, query, builder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            FetchParent<Object, Object> f = traverseIncludePath(root, path);
            Join join = (Join) f;

            query.distinct(true);

            return join.getOn();
        };
    }

    private FetchParent<Object, Object> traverseIncludePath(FetchParent<?, ?> root, PropertyPath path) {
        FetchParent<Object, Object> result = root.fetch(path.getSegment(), JoinType.LEFT);
        return path.hasNext() ? traverseIncludePath(result, Objects.requireNonNull(path.next())) : result;
    }

    private Specification<Object> buildEqSpec(Object pathVarValue, String attributePath) {
        return (Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            PropertyPath path = PropertyPath.from(attributePath, root.getJavaType());
            return criteriaBuilder.equal(traverseEqPath(root, path), pathVarValue);
        };
    }

    private Expression<Object> traverseEqPath(Path<?> root, PropertyPath path) {
        Path<Object> result = root.get(path.getSegment());
        return path.hasNext() ? traverseEqPath(result, Objects.requireNonNull(path.next())) : result;
    }

}
