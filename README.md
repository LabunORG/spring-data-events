# spring-data-events

An implementation of missing spring data repository hooks.
JPA @EventHandlers have following limitations, which
have been solved in this library:
1) It leads to circular dependency between your entity and
it's event handler which makes it impossible to split your
application into dedicated modules or extends an existing
one with own modules.
2) The inability to inject spring beans without using of
static context.

## How to use

Thanks to SpringBoot AutoConfiguration all you need to start
is add library as dependency to your pom.xml:

```xml
  <dependency>
    <groupId>org.labun</groupId>
    <artifactId>spring-data-events</artifactId>
    <version>1.0.0</version>
  </dependency>
```

Usage Example:

```java
@RepositoryEventHandler
public class PersonRepositoryEventHandler {

    @BeforeCreate
    public void updateStatusBeforeCreate(Person person) {
        // do something very important...
    }

    @AfterCreate
    public void updateStatusAfterCreate(Person person) {
        // do something very important...
    }

    @BeforeUpdate
    public void updateStatusBeforeUpdate(Person person) {
        // do something very important...
    }

    @AfterUpdate
    public void updateStatusAfterUpdate(Person person) {
        // do something very important...
    }

    @BeforeDelete
    public void updateStatusBeforeDelete(Person person) {
        // do something very important...
    }

    @AfterDelete
    public void updateStatusAfterDelete(Person person) {
        // do something very important...
    }
}
```
