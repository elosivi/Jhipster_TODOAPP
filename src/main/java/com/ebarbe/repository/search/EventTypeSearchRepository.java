package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.EventType;
import com.ebarbe.repository.EventTypeRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link EventType} entity.
 */
public interface EventTypeSearchRepository extends ElasticsearchRepository<EventType, Long>, EventTypeSearchRepositoryInternal {}

interface EventTypeSearchRepositoryInternal {
    Stream<EventType> search(String query);

    Stream<EventType> search(Query query);

    @Async
    void index(EventType entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EventTypeSearchRepositoryInternalImpl implements EventTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EventTypeRepository repository;

    EventTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EventTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<EventType> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<EventType> search(Query query) {
        return elasticsearchTemplate.search(query, EventType.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(EventType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), EventType.class);
    }
}
