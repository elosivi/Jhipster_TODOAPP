package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Status;
import com.ebarbe.repository.StatusRepository;
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
 * Spring Data Elasticsearch repository for the {@link Status} entity.
 */
public interface StatusSearchRepository extends ElasticsearchRepository<Status, Long>, StatusSearchRepositoryInternal {}

interface StatusSearchRepositoryInternal {
    Page<Status> search(String query, Pageable pageable);

    Page<Status> search(Query query);

    @Async
    void index(Status entity);

    @Async
    void deleteFromIndexById(Long id);
}

class StatusSearchRepositoryInternalImpl implements StatusSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final StatusRepository repository;

    StatusSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, StatusRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Status> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Status> search(Query query) {
        SearchHits<Status> searchHits = elasticsearchTemplate.search(query, Status.class);
        List<Status> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Status entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Status.class);
    }
}
