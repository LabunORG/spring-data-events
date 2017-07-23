package org.labun.springframework.data.repository.events.support;

import org.labun.springframework.data.repository.events.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RepositoryEventHandler
public class PersonRepositoryEventHandler {
    public static final String AFTER_UPDATE = "AFTER UPDATE";
    public static final String BEFORE_DELETE = "BEFORE DELETE";
    public static final String AFTER_DELETE = "AFTER DELETE";
    public static final String BEFORE_UPDATE = "BEFORE UPDATE";
    public static final String BEFORE_CREATE = "BEFORE CREATE";
    public static final String AFTER_CREATE = "AFTER CREATE";

    private List<String> events = new ArrayList<>();

    @BeforeCreate
    public void updateStatusBeforeCreate(Person person) {
        events.add(BEFORE_CREATE);
    }

    @AfterCreate
    public void updateStatusAfterCreate(Person person) {
        events.add(AFTER_CREATE);
    }

    @BeforeUpdate
    public void updateStatusBeforeUpdate(Person person) {
        events.add(BEFORE_UPDATE);
    }

    @AfterUpdate
    public void updateStatusAfterUpdate(Person person) {
        events.add(AFTER_UPDATE);
    }

    @BeforeDelete
    public void updateStatusBeforeDelete(Person person) {
        events.add(BEFORE_DELETE);
    }

    @AfterDelete
    public void updateStatusAfterDelete(Person person) {
        events.add(AFTER_DELETE);
    }

    public List<String> getEvents() {
        return events;
    }
}
