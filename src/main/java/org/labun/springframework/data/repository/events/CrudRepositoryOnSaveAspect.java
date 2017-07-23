package org.labun.springframework.data.repository.events;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.labun.springframework.data.repository.events.event.AfterCreateEvent;
import org.labun.springframework.data.repository.events.event.AfterUpdateEvent;
import org.labun.springframework.data.repository.events.event.BeforeCreateEvent;
import org.labun.springframework.data.repository.events.event.BeforeUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A Delete action aspect for CrudRepository. Emits {@link org.labun.springframework.data.repository.events.event.RepositoryEvent}
 * before and after each create and update operations of CrudRepository.
 */
@Component
@Aspect
public class CrudRepositoryOnSaveAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final EntityInformationProvider entityInformationProvider;

    @Autowired
    public CrudRepositoryOnSaveAspect(ApplicationEventPublisher eventPublisher,
                                      EntityInformationProvider entityInformationProvider) {
        this.eventPublisher = eventPublisher;
        this.entityInformationProvider = entityInformationProvider;
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.save(..))" +
            "&& !execution(* org.springframework.data.repository.CrudRepository.save(Iterable))")
    public void saveOne() {
    }

    @Around("saveOne() && args(entity) && target(target)")
    public Object onSave(CrudRepository target, Object entity) {
        return saveEntity(target, entity);
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.save(Iterable))")
    public void saveIterable() {
    }

    @Around("saveIterable() && args(iterable) && target(target)")
    public Iterable<Object> onSave(CrudRepository target, Iterable iterable) {
        return saveEntities(target, (Iterable<?>) iterable);
    }

    private Iterable<Object> saveEntities(CrudRepository target, Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(entity -> saveEntity(target, entity))
                .collect(Collectors.toList());
    }

    private Object saveEntity(CrudRepository target, Object entity) {
        EntityInformation entityInformation = entityInformationProvider.getEntityInformationFor(entity.getClass());
        return entityInformation.isNew(entity) ?
                createEntity(target, entity) : updateEntity(target, entity);
    }

    private Object updateEntity(CrudRepository repository, Object entity) {
        eventPublisher.publishEvent(new BeforeUpdateEvent(entity));
        Object result = repository.save(entity);
        eventPublisher.publishEvent(new AfterUpdateEvent(entity));
        return result;
    }

    private Object createEntity(CrudRepository repository, Object entity) {
        eventPublisher.publishEvent(new BeforeCreateEvent(entity));
        Object result = repository.save(entity);
        eventPublisher.publishEvent(new AfterCreateEvent(entity));
        return result;
    }
}
