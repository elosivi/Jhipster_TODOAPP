package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Event;
import com.ebarbe.repository.EventRepository;
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
 * Spring Data Elasticsearch repository for the {@link Event} entity.
 */
public interface EventSearchRepository extends ElasticsearchRepository<Event, Long>, EventSearchRepositoryInternal {}

interface EventSearchRepositoryInternal {
    Page<Event> search(String query, Pageable pageable);

    Page<Event> search(Query query);

    @Async
    void index(Event entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EventSearchRepositoryInternalImpl implements EventSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EventRepository repository;

    EventSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EventRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Event> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Event> search(Query query) {
        SearchHits<Event> searchHits = elasticsearchTemplate.search(query, Event.class);
        List<Event> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Event entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Event.class);
    }
}
