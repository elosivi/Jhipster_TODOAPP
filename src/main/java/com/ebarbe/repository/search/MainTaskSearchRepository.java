package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.MainTask;
import com.ebarbe.repository.MainTaskRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link MainTask} entity.
 */
public interface MainTaskSearchRepository extends ElasticsearchRepository<MainTask, Long>, MainTaskSearchRepositoryInternal {}

interface MainTaskSearchRepositoryInternal {
    Stream<MainTask> search(String query);

    Stream<MainTask> search(Query query);

    @Async
    void index(MainTask entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MainTaskSearchRepositoryInternalImpl implements MainTaskSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MainTaskRepository repository;

    MainTaskSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MainTaskRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<MainTask> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<MainTask> search(Query query) {
        return elasticsearchTemplate.search(query, MainTask.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(MainTask entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), MainTask.class);
    }
}
