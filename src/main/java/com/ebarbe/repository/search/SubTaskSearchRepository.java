package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.SubTask;
import com.ebarbe.repository.SubTaskRepository;
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
 * Spring Data Elasticsearch repository for the {@link SubTask} entity.
 */
public interface SubTaskSearchRepository extends ElasticsearchRepository<SubTask, Long>, SubTaskSearchRepositoryInternal {}

interface SubTaskSearchRepositoryInternal {
    Page<SubTask> search(String query, Pageable pageable);

    Page<SubTask> search(Query query);

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
    public Page<SubTask> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SubTask> search(Query query) {
        SearchHits<SubTask> searchHits = elasticsearchTemplate.search(query, SubTask.class);
        List<SubTask> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
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
