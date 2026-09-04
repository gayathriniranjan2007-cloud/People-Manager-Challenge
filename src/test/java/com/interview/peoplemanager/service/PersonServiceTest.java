package com.interview.peoplemanager.service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.repository.PersonRepository;

class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchByLastName_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Person person = new Person("John", "Doe", new Date(375485579000L));
        Page<Person> expectedPage = new PageImpl<>(List.of(person), pageable, 1);

        when(personRepository.findByLastNameIgnoreCase("doe", pageable)).thenReturn(expectedPage);

        Page<Person> result = personService.searchByLastName("  doe  ", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
        verify(personRepository, times(1)).findByLastNameIgnoreCase("doe", pageable);
    }

    @Test
    void testSearchByLastName_BlankOrNull_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Person> resultNull = personService.searchByLastName(null, pageable);
        assertThat(resultNull).isEmpty();

        Page<Person> resultBlank = personService.searchByLastName("   ", pageable);
        assertThat(resultBlank).isEmpty();

        verify(personRepository, never()).findByLastNameIgnoreCase(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void testGetAllPeople() {
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personRepository.findAll()).thenReturn(Collections.singletonList(person));

        List<Person> people = personService.getAllPeople();

        assertThat(people).hasSize(1);
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetPersonById() {
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personRepository.findById(1)).thenReturn(Optional.of(person));

        Optional<Person> found = personService.getPersonById(1);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
    }

    @Test
    void testSavePerson() {
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personRepository.save(person)).thenReturn(person);

        Person saved = personService.savePerson(person);

        assertThat(saved).isEqualTo(person);
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testDeletePerson() {
        personService.deletePerson(1);
        verify(personRepository, times(1)).deleteById(1);
    }
}
