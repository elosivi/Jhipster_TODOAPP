package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Person;
import com.ebarbe.repository.PersonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
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
import org.springframework.stereotype.Component;

/**
 * Spring Data Elasticsearch repository for the {@link Person} entity.
 */

public interface PersonSearchRepository extends ElasticsearchRepository<Person, Long>, PersonSearchRepositoryInternal {}

interface PersonSearchRepositoryInternal {
    Page<Person> search(String query, Pageable pageable);

    Page<Person> search(Query query);

    @Async
    void index(Person entity);

    @Async
    void deleteFromIndexById(Long id);
}

@Component
@Primary
class PersonSearchRepositoryInternalImpl implements PersonSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PersonRepository repository;

    @Autowired
    PersonSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        @Qualifier("personRepository") PersonRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Person> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Person> search(Query query) {
        SearchHits<Person> searchHits = elasticsearchTemplate.search(query, Person.class);
        List<Person> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Person entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Person.class);
    }
}
