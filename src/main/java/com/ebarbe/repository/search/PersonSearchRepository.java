package com.ebarbe.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ebarbe.domain.Person;
import com.ebarbe.repository.PersonRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Person} entity.
 */
public interface PersonSearchRepository extends ElasticsearchRepository<Person, Long>, PersonSearchRepositoryInternal {}

interface PersonSearchRepositoryInternal {
    Stream<Person> search(String query);

    Stream<Person> search(Query query);

    @Async
    void index(Person entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PersonSearchRepositoryInternalImpl implements PersonSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PersonRepository repository;

    PersonSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PersonRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Person> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Person> search(Query query) {
        return elasticsearchTemplate.search(query, Person.class).map(SearchHit::getContent).stream();
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
