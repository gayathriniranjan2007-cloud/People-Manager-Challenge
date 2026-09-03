package com.interview.peoplemanager.repository;

import org.springframework.data.repository.CrudRepository;

import com.interview.peoplemanager.model.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
}
