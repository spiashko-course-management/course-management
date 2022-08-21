package com.spiashko.cm.utils;

import org.springframework.lang.Nullable;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortUtils {

    private static final String MULTIPLE_SORT_SEPARATOR = ";";
    private static final String SORT_SEPARATOR = ",";
    private static final String PROPERTY_PATH_SEPARATOR = "\\.";

    public static List<Order> parseSort(@Nullable final String sort, final Root<?> root, final CriteriaBuilder cb) {
        if (sort == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(sort.split(MULTIPLE_SORT_SEPARATOR))
            .map(item -> item.split(SORT_SEPARATOR))
            .map(parts -> sortToJpaOrder(parts, root, cb))
            .collect(Collectors.toList());
    }

    private static Order sortToJpaOrder(final String[] parts, final Root<?> root, final CriteriaBuilder cb) {
        if (parts.length < 2) {
            throw new RuntimeException("to define sort you should at least define direction and property");
        }

        final String propertyPath = parts[0];
        final String direction = parts[1];

        Path<?> propertyPathExpression = pathToExpression(root, propertyPath);

        Expression<?> propertyExpression = parts.length == 2 ? propertyPathExpression :
            buildExtendedExpression(parts[2], Arrays.copyOfRange(parts, 2, parts.length), propertyPathExpression, cb);

        return direction.equalsIgnoreCase("asc") ? cb.asc(propertyExpression) : cb.desc(propertyExpression);
    }

    @SuppressWarnings("unchecked")
    private static Expression<?> buildExtendedExpression(String sortOperation, String[] args,
                                                         Path<?> propertyPath, CriteriaBuilder cb) {
        return switch (sortOperation) {
            case "ic" -> cb.lower((Expression<String>) propertyPath);
            case "trgm" -> cb.function("SIMILARITY", Double.class, cb.literal(args[0]), propertyPath);
            default -> throw new IllegalArgumentException("Unrecognized sortOperation");
        };
    }

    private static Path<?> pathToExpression(final Root<?> root, final String path) {
        final String[] properties = path.split(PROPERTY_PATH_SEPARATOR);

        Path<?> expression = root.get(properties[0]);
        for (int i = 1; i < properties.length; ++i) {
            expression = expression.get(properties[i]);
        }
        return expression;
    }

}
