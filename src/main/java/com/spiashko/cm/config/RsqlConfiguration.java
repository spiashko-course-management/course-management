package com.spiashko.cm.config;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLCustomPredicate;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RsqlConfiguration {

    @Bean
    public List<RSQLCustomPredicate<?>> customPredicates() {
        return List.of(
            new RSQLCustomPredicate<>(
                new ComparisonOperator("=trgm=", true),
                String.class,
                (input) -> {
                    val arguments = input.getArguments();
                    if (arguments.size() != 2) {
                        throw new RuntimeException(
                            String.format("=trgm= expects exactly two arguments " +
                                    "but was provided %s with values %s",
                                arguments.size(), arguments)
                        );
                    }
                    val cb = input.getCriteriaBuilder();
                    val path = input.getPath();
                    return cb.greaterThan(cb.function("SIMILARITY", Double.class,
                        cb.literal(arguments.get(0)), path), Double.parseDouble((String) arguments.get(1)));
                }
            )
        );
    }
}
