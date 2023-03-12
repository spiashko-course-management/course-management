package com.spiashko.cm.utils;

import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import com.spiashko.rfetch.parser.RfetchNode;
import com.spiashko.rfetch.parser.RfetchSupport;
import io.github.perplexhub.rsql.RSQLCustomPredicate;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FetchUtils {

    private final List<RSQLCustomPredicate<?>> customPredicates;
    private final SmartFetchTemplate fetchSmartTemplate;

    public <T> Page<T> fetchPage(JpaSpecificationExecutor<T> repo,
                                 FetchPageRequest pageRequest,
                                 Class<T> clazz) {
        Specification<T> spec = RSQLJPASupport.rsql(pageRequest.getFilter(), customPredicates);
        RfetchNode rfetchNode = RfetchSupport.compile(pageRequest.getInclude(), clazz);
        Specification<T> customSortSpec = SortUtils.parseSort(pageRequest.getSort());
        spec = spec.and(customSortSpec);
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        return fetchSmartTemplate.fetchPage(rfetchNode, repo, spec, pageable);
    }

    public <T> Optional<T> fetchOne(JpaSpecificationExecutor<T> repo,
                                    FetchRequest fetchRequest,
                                    Class<T> clazz) {
        Specification<T> spec = RSQLJPASupport.rsql(fetchRequest.getFilter(), customPredicates);
        RfetchNode rfetchNode = RfetchSupport.compile(fetchRequest.getInclude(), clazz);
        return fetchSmartTemplate.fetchOne(rfetchNode, repo, spec);
    }

    public <T, ID> Optional<T> fetchById(JpaSpecificationExecutor<T> repo, SingularAttribute<T, ID> idAttr, ID id,
                                         IncludeRequest includeRequest,
                                         Class<T> clazz) {
        RfetchNode rfetchNode = RfetchSupport.compile(includeRequest.getInclude(), clazz);
        return fetchSmartTemplate.fetchOne(rfetchNode, repo,
            (root, query, builder) -> builder.equal(root.get(idAttr), id));
    }

    @ToString
    @Getter
    @Setter
    public static class FetchPageRequest {
        private String filter;
        private String include;
        private String sort;
        @PositiveOrZero
        private int page = 0;
        @Positive
        private int size = 5;
    }

    @ToString
    @Getter
    @Setter
    public static class FetchRequest {
        private String filter;
        private String include;
    }

    @ToString
    @Getter
    @Setter
    public static class IncludeRequest {
        private String include;
    }
}
