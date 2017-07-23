package org.labun.springframework.data.repository.events;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.labun.springframework.data.repository.events.event.AfterDeleteEvent;
import org.labun.springframework.data.repository.events.event.BeforeDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * A Delete action aspect for CrudRepository. Emits {@link org.labun.springframework.data.repository.events.event.RepositoryEvent}
 * before and after each delete operations of CrudRepository.
 */
@Component
@Aspect
public class CrudRepositoryOnDeleteAspect {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CrudRepositoryOnDeleteAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.delete(..))" +
            "&& !execution(* org.springframework.data.repository.CrudRepository.delete(java.io.Serializable))" +
            "&& !execution(* org.springframework.data.repository.CrudRepository.delete(Iterable))")
    public void deleteOne() {
    }

    @Around("deleteOne() && args(entity) && target(target)")
    public void onDeleteOne(CrudRepository target, Object entity) {
        if (entity == null) return;
        deleteEntity(target, entity);
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.delete(Iterable))")
    public void deleteIterable() {
    }

    @Around("deleteIterable() && args(iterable) && target(target)")
    public void onDeleteIterable(CrudRepository target, Iterable iterable) {
        if (iterable == null) return;
        deleteEntities(target, (Iterable<?>) iterable);
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.delete(java.io.Serializable))")
    public void deleteOneById() {
    }

    @Around("deleteOneById() && args(id) && target(target)")
    public void onDeleteOneById(CrudRepository target, Serializable id) {
        if (id == null) return;
        deleteById(target, id);
    }

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.deleteAll())")
    public void deleteAll() {
    }

    @Around("deleteAll() && target(target)")
    public void onDeleteAll(CrudRepository target) {
        target.findAll().forEach(it -> deleteEntity(target, it));
    }

    private void deleteById(CrudRepository target, Serializable id) {
        Object entity = target.findOne(id);
        if (entity == null) return;

        deleteEntity(target, entity);
    }

    private void deleteEntities(CrudRepository target, Iterable<?> iterable) {
        iterable.forEach(entity -> deleteEntity(target, entity));
    }

    private void deleteEntity(CrudRepository target, Object entity) {
        eventPublisher.publishEvent(new BeforeDeleteEvent(entity));
        target.delete(entity);
        eventPublisher.publishEvent(new AfterDeleteEvent(entity));
    }
}
