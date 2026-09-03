# People Manager Challenge

Spring Boot CRUD application built per the "People Manager Challenge" instructions.

- Group id: `com.interview`, artifact id: `people-manager`
- Spring Web + Spring Data JPA + MySQL Connector/J (Spring Boot 3.3.5, Java 17+)
- Entity `Person` (id, name, lastName, birthdate) with `GenerationType.IDENTITY`
- `PersonRepository` extends `CrudRepository<Person, Integer>`
- `PersonService` with `getAllPeople`, `getPersonById`, `savePerson`, `deletePerson`
- `PersonController` REST endpoints under `/people` returning `ResponseEntity`
  (GET all → OK, GET by id → OK / NOT_FOUND, POST → CREATED, PUT → OK / NOT_FOUND, DELETE → NO_CONTENT)
- Provided unit tests included verbatim: `PersonRepositoryTest`, `PersonControllerTest`

## Prerequisites

- JDK 17+ (Spring Boot 3 requires 17; JDK 11 works only if you downgrade to Boot 2.7 and switch `jakarta.persistence` imports back to `javax.persistence` in `Person.java`)
- MySQL Server 8+ running on `localhost:3306`
- Maven 3.6+

## Setup

1. Create the schema:

   ```sql
   CREATE DATABASE IF NOT EXISTS peopledb;
   ```

2. Set your MySQL credentials in `src/main/resources/application.properties`
   (`spring.datasource.username` / `spring.datasource.password`).

## Run the tests (Step 8)

```bash
mvn test
```

- `PersonRepositoryTest` runs against the real MySQL database (`@AutoConfigureTestDatabase(replace = NONE)`, `@Rollback(false)`), so after it passes you can verify in MySQL:

  ```sql
  USE peopledb;
  DESCRIBE person;                 -- id, name, last_name, birthdate
  SELECT * FROM person;            -- John Smith and Jane Smith inserted
  ```

- `PersonControllerTest` is a pure Mockito test (no DB needed).

## Run the application (Step 9)

```bash
mvn spring-boot:run
```

Then test with Postman/curl:

- `GET    http://localhost:8080/people/`
- `GET    http://localhost:8080/people/1`
- `POST   http://localhost:8080/people/`  body: `{"name":"Ada","lastName":"Lovelace","birthdate":"1815-12-10"}`
- `PUT    http://localhost:8080/people/1` body: `{"name":"John","lastName":"Updated","birthdate":"1981-11-25"}`
- `DELETE http://localhost:8080/people/2`
