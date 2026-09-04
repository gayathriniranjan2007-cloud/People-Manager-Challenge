package com.interview.peoplemanager.repository;


import java.sql.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.interview.peoplemanager.model.Person;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class PersonRepositoryTest {
    @Autowired
    private PersonRepository repo;

    @Test
    public void testAddNew(){
        Person person    = new Person("John","Smith", new Date(375485579000L));
        Person savedPerson = repo.save(person);
        Assertions.assertThat(savedPerson).isNotNull();
        person    = new Person("Jane","Smith", new Date(474327179000L));
        savedPerson = repo.save(person);
        Assertions.assertThat(savedPerson).isNotNull();
        Assertions.assertThat(savedPerson.getId()).isGreaterThan(0);

    }

    @Test
    public void testFindByLastNameIgnoreCasePaginated() {
        repo.save(new Person("Alice", "Taylor", new Date(375485579000L)));
        repo.save(new Person("Bob", "Taylor", new Date(474327179000L)));

        org.springframework.data.domain.Page<Person> page = repo.findByLastNameIgnoreCase(
                "taylor",
                org.springframework.data.domain.PageRequest.of(0, 10)
        );

        Assertions.assertThat(page).isNotNull();
        Assertions.assertThat(page.getContent()).isNotEmpty();
        Assertions.assertThat(page.getContent()).allMatch(p -> "Taylor".equalsIgnoreCase(p.getLastName()));
    }
}
