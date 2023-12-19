package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Hierarchy} entity.
 */
public interface HierarchySearchRepository extends ElasticsearchRepository<Hierarchy, Long>, HierarchySearchRepositoryInternal {}

interface HierarchySearchRepositoryInternal {
    Stream<Hierarchy> search(String query);

    Stream<Hierarchy> search(Query query);

    @Async
    void index(Hierarchy entity);

    @Async
    void deleteFromIndexById(Long id);
}

class HierarchySearchRepositoryInternalImpl implements HierarchySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final HierarchyRepository repository;

    HierarchySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, HierarchyRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Hierarchy> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Hierarchy> search(Query query) {
        return elasticsearchTemplate.search(query, Hierarchy.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Hierarchy entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Hierarchy.class);
    }
}
