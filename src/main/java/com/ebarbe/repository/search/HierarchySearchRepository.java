package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.repository.HierarchyRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Hierarchy} entity.
 */
public interface HierarchySearchRepository extends ElasticsearchRepository<Hierarchy, Long>, HierarchySearchRepositoryInternal {}

interface HierarchySearchRepositoryInternal {
    Page<Hierarchy> search(String query, Pageable pageable);

    Page<Hierarchy> search(Query query);

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
    public Page<Hierarchy> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Hierarchy> search(Query query) {
        SearchHits<Hierarchy> searchHits = elasticsearchTemplate.search(query, Hierarchy.class);
        List<Hierarchy> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
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
