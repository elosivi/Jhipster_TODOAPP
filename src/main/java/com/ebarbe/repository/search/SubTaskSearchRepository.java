package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SubTask} entity.
 */
public interface SubTaskSearchRepository extends ElasticsearchRepository<SubTask, Long>, SubTaskSearchRepositoryInternal {}

interface SubTaskSearchRepositoryInternal {
    Stream<SubTask> search(String query);

    Stream<SubTask> search(Query query);

    @Async
    void index(SubTask entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SubTaskSearchRepositoryInternalImpl implements SubTaskSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SubTaskRepository repository;

    SubTaskSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SubTaskRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<SubTask> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<SubTask> search(Query query) {
        return elasticsearchTemplate.search(query, SubTask.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(SubTask entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SubTask.class);
    }
}
