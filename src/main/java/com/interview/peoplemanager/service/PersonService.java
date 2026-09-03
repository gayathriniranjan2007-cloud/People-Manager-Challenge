package com.interview.peoplemanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interview.peoplemanager.model.Person;
import com.interview.peoplemanager.repository.PersonRepository;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPeople() {
        List<Person> people = new ArrayList<>();
        personRepository.findAll().forEach(people::add);
        return people;
    }

    public Optional<Person> getPersonById(Integer id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Integer id) {
        personRepository.deleteById(id);
    }
}
