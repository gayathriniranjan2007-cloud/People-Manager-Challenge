package com.interview.peoplemanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.repository.PersonRepository;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public PersonService() {
    }

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPeople() {
        List<Person> people = new ArrayList<>();
        personRepository.findAll().forEach(people::add);
        return people;
    }

    public Optional<Person> getPersonById(Integer id) {
        return personRepository.findById(id);
    }

    public Page<Person> searchByLastName(String lastName, Pageable pageable) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        return personRepository.findByLastNameIgnoreCase(lastName.trim(), pageable);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Integer id) {
        personRepository.deleteById(id);
    }
}
