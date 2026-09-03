package com.interview.peoplemanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.service.PersonService;

class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPeople() {

        List<Person> people = Arrays.asList(
                new Person("John", "Doe", new Date(375485579000L)),
                new Person("Jane", "Doe", new Date(474327179000L)));
        when(personService.getAllPeople()).thenReturn(people);

        ResponseEntity<List<Person>> response = personController.getAllPeople();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(people, response.getBody());
    }

    @Test
    void testGetPersonByIdFound() {
        int id = 1;
        Person person = new Person("John",
                "Doe",
                new Date(375485579000L));
        when(personService.getPersonById(id)).thenReturn(Optional.of(person));

        ResponseEntity<Person> response = personController.getPersonById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    void testGetPersonByIdNotFound() {
        int id = 1;
        when(personService.getPersonById(id)).thenReturn(Optional.empty());

        ResponseEntity<Person> response = personController.getPersonById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreatePerson() {
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personService.savePerson(person)).thenReturn(person);

        ResponseEntity<Person> response = personController.createPerson(person);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    void testUpdatePersonFound() {
        int id = 1;
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personService.getPersonById(id)).thenReturn(Optional.of(person));
        when(personService.savePerson(person)).thenReturn(person);

        ResponseEntity<Person> response = personController.updatePerson(id, person);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    void testUpdatePersonNotFound() {
        int id = 1;
        Person person = new Person("John", "Doe", new Date(375485579000L));
        when(personService.getPersonById(id)).thenReturn(Optional.empty());

        ResponseEntity<Person> response = personController.updatePerson(id, person);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeletePerson() {
        int id = 1;
        ResponseEntity<Void> response = personController.deletePerson(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(personService, times(1)).deletePerson(id);
    }
}
