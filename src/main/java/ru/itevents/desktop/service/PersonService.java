package ru.itevents.desktop.service;

import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.repository.PersonRepository;

import java.util.List;

public class PersonService {
    private final PersonRepository repository = new PersonRepository();

    public List<Person> getPeople(PersonRole role) {
        return repository.findByRole(role);
    }

    public List<Person> search(PersonRole role, String query) {
        return repository.search(role, query);
    }

    public Person save(Person person) {
        if (person.getId() == null) {
            long id = repository.insert(person);
            person.setId(id);
        } else {
            repository.update(person);
        }
        return person;
    }

    public void delete(long id) {
        repository.delete(id);
    }

    public Person findByName(String name) {
        return repository.findByFullName(name);
    }
}
