package com.interview.peoplemanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.peoplemanager.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Page<Person> findByLastNameIgnoreCase(String lastName, Pageable pageable);
}
