package org.labun.springframework.data.repository.events;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.labun.springframework.data.repository.events.support.Person;
import org.labun.springframework.data.repository.events.support.PersonRepository;
import org.labun.springframework.data.repository.events.support.PersonRepositoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.labun.springframework.data.repository.events.support.PersonRepositoryEventHandler.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringEventsTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonRepositoryEventHandler eventHandler;

    @Before
    public void setUp() {
        eventHandler.getEvents().clear();
    }

    @Test
    public void testEventsForSingleEntity() {
        Person person = createPerson();

        // Create
        personRepository.save(person);
        Assert.assertEquals(Arrays.asList(BEFORE_CREATE, AFTER_CREATE),
                eventHandler.getEvents());
        eventHandler.getEvents().clear();

        // Update
        person.setLastName("Changed");
        personRepository.save(person);
        Assert.assertEquals(Arrays.asList(BEFORE_UPDATE, AFTER_UPDATE),
                eventHandler.getEvents());
        eventHandler.getEvents().clear();

        // Delete
        personRepository.delete(person);
        Assert.assertEquals(Arrays.asList(BEFORE_DELETE, AFTER_DELETE),
                eventHandler.getEvents());
    }

    @Test
    public void testEventsForSingleEntityDeleteById() {
        Person person = createPerson();

        person = personRepository.save(person);

        // Delete
        personRepository.delete(person.getId());
        Assert.assertEquals(Arrays.asList(BEFORE_CREATE, AFTER_CREATE,
                BEFORE_DELETE, AFTER_DELETE), eventHandler.getEvents());
    }

    @Test
    public void testEventsForEntityCollection() {
        List<Person> people = Arrays.asList(createPerson(),
                createPerson(), createPerson());


        // Create
        personRepository.save(people);
        Assert.assertEquals(Arrays.asList(
                BEFORE_CREATE, AFTER_CREATE,
                BEFORE_CREATE, AFTER_CREATE,
                BEFORE_CREATE, AFTER_CREATE),
                eventHandler.getEvents());
        eventHandler.getEvents().clear();

        // Update
        for (Person person : people) {
            person.setLastName("Changed");
        }
        personRepository.save(people);
        Assert.assertEquals(Arrays.asList(
                BEFORE_UPDATE, AFTER_UPDATE,
                BEFORE_UPDATE, AFTER_UPDATE,
                BEFORE_UPDATE, AFTER_UPDATE),
                eventHandler.getEvents());
        eventHandler.getEvents().clear();

        // Delete
        personRepository.delete(people);
        Assert.assertEquals(Arrays.asList(
                BEFORE_DELETE, AFTER_DELETE,
                BEFORE_DELETE, AFTER_DELETE,
                BEFORE_DELETE, AFTER_DELETE),
                eventHandler.getEvents());
    }

    private Person createPerson() {
        Person person = new Person();
        person.setFirstName("Konstantin");
        person.setLastName("Labun");
        return person;
    }

    @Configuration
    @SpringBootApplication
    public static class SpringEventsTestConfiguration {
    }
}
