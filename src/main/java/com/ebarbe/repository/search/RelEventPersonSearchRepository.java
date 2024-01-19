package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.repository.RelEventPersonRepository;
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
 * Spring Data Elasticsearch repository for the {@link RelEventPerson} entity.
 */
public interface RelEventPersonSearchRepository
    extends ElasticsearchRepository<RelEventPerson, Long>, RelEventPersonSearchRepositoryInternal {}

interface RelEventPersonSearchRepositoryInternal {
    Page<RelEventPerson> search(String query, Pageable pageable);

    Page<RelEventPerson> search(Query query);

    @Async
    void index(RelEventPerson entity);

    /**
     * prefer to use a method that uses eventid + personid
     * @param id
     */
    @Async
    void deleteFromIndexById(Long id);
}

class RelEventPersonSearchRepositoryInternalImpl implements RelEventPersonSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;

    RelEventPersonSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, RelEventPersonRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<RelEventPerson> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<RelEventPerson> search(Query query) {
        SearchHits<RelEventPerson> searchHits = elasticsearchTemplate.search(query, RelEventPerson.class);
        List<RelEventPerson> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(RelEventPerson entity) {
        // not implemented because no siply index available
        // prefer to use a method that uses eventid + personid

    }

    @Override
    public void deleteFromIndexById(Long id) {
        // not implemented because no siply index available
        // prefer to use a method that uses eventid + personid
    }
}
