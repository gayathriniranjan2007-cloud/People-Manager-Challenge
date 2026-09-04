package com.interview.peoplemanager.controller;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.interview.peoplemanager.exception.GlobalExceptionHandler;
import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.service.PersonService;

class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(personController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testGetAllPeople() throws Exception {
        List<Person> people = Arrays.asList(
                new Person("John", "Doe", Date.valueOf("1980-01-01")),
                new Person("Jane", "Doe", Date.valueOf("1985-05-15")));
        when(personService.getAllPeople()).thenReturn(people);

        mockMvc.perform(get("/people/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[1].name", is("Jane")));
    }

    @Test
    void testGetPersonByIdFound() throws Exception {
        int id = 1;
        Person person = new Person("John", "Doe", Date.valueOf("1980-01-01"));
        person.setId(id);
        when(personService.getPersonById(id)).thenReturn(Optional.of(person));

        mockMvc.perform(get("/people/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void testGetPersonByIdNotFound() throws Exception {
        int id = 99;
        when(personService.getPersonById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/people/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchPeopleByLastName_Paginated() throws Exception {
        Person person1 = new Person("John", "Doe", Date.valueOf("1980-01-01"));
        person1.setId(1);
        Page<Person> pageResult = new PageImpl<>(List.of(person1), PageRequest.of(0, 10), 1);

        when(personService.searchByLastName(eq("Doe"), any())).thenReturn(pageResult);

        mockMvc.perform(get("/people/search")
                        .param("lastName", "Doe")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("John")))
                .andExpect(jsonPath("$.content[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(personService, times(1)).searchByLastName(eq("Doe"), any());
    }

    @Test
    void testCreatePerson_Success() throws Exception {
        Person saved = new Person("John", "Doe", Date.valueOf("1980-01-01"));
        saved.setId(1);
        when(personService.savePerson(any(Person.class))).thenReturn(saved);

        String validJson = "{\"name\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(post("/people/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));

        verify(personService, times(1)).savePerson(any(Person.class));
    }

    @Test
    void testCreatePerson_BlankName_Returns400() throws Exception {
        String invalidJson = "{\"name\":\"  \",\"lastName\":\"Doe\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(post("/people/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.name", containsString("First name is mandatory")));

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testCreatePerson_BlankLastName_Returns400() throws Exception {
        String invalidJson = "{\"name\":\"John\",\"lastName\":\"\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(post("/people/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.lastName", containsString("Last name is mandatory")));

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testCreatePerson_MissingBirthdate_Returns400() throws Exception {
        String invalidJson = "{\"name\":\"John\",\"lastName\":\"Doe\"}";

        mockMvc.perform(post("/people/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.birthdate", containsString("Birthdate is mandatory")));

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testCreatePerson_MalformedDateFormat_Returns400() throws Exception {
        String invalidJson = "{\"name\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"not-a-valid-date\"}";

        mockMvc.perform(post("/people/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Malformed JSON request or invalid field format")));

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testUpdatePerson_Success() throws Exception {
        int id = 1;
        Person existing = new Person("John", "Doe", Date.valueOf("1980-01-01"));
        existing.setId(id);
        Person updated = new Person("Johnny", "Doe", Date.valueOf("1980-01-01"));
        updated.setId(id);

        when(personService.getPersonById(id)).thenReturn(Optional.of(existing));
        when(personService.savePerson(any(Person.class))).thenReturn(updated);

        String validJson = "{\"name\":\"Johnny\",\"lastName\":\"Doe\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(put("/people/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Johnny")));

        verify(personService, times(1)).savePerson(any(Person.class));
    }

    @Test
    void testUpdatePerson_InvalidPerson_Returns400() throws Exception {
        int id = 1;
        String invalidJson = "{\"name\":\"\",\"lastName\":\"Doe\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(put("/people/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.name", containsString("First name is mandatory")));

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testUpdatePerson_NotFound() throws Exception {
        int id = 99;
        when(personService.getPersonById(id)).thenReturn(Optional.empty());

        String validJson = "{\"name\":\"John\",\"lastName\":\"Doe\",\"birthdate\":\"1980-01-01\"}";

        mockMvc.perform(put("/people/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isNotFound());

        verify(personService, never()).savePerson(any(Person.class));
    }

    @Test
    void testDeletePerson() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/people/{id}", id))
                .andExpect(status().isNoContent());

        verify(personService, times(1)).deletePerson(id);
    }
}
