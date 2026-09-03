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
}
