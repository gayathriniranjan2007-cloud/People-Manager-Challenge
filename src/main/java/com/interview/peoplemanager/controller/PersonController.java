package com.interview.peoplemanager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.service.PersonService;

@RestController
@RequestMapping("/people")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/")
    public ResponseEntity<List<Person>> getAllPeople() {
        List<Person> people = personService.getAllPeople();
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Integer id) {
        Optional<Person> person = personService.getPersonById(id);
        return person.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person savedPerson = personService.savePerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Integer id, @RequestBody Person person) {
        Optional<Person> existingPerson = personService.getPersonById(id);
        if (existingPerson.isPresent()) {
            person.setId(id);
            Person updatedPerson = personService.savePerson(person);
            return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
