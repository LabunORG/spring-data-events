/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labun.springframework.data.repository.events;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.labun.springframework.data.repository.events.annotation.*;
import org.labun.springframework.data.repository.events.event.*;
import org.labun.springframework.data.repository.events.utils.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Component to discover annotated repository event handlers and trigger them on {@link ApplicationEvent}s.
 *
 * @author Jon Brisbin
 * @author Oliver Gierke
 * @author Konstantin Labun
 */
public class AnnotatedEventHandlerInvoker implements ApplicationListener<RepositoryEvent>, BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedEventHandlerInvoker.class);
    private static final String PARAMETER_MISSING = "Invalid event handler method %s! At least a single argument is required to determine the domain type for which you are interested in events.";

    private final MultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<Class<? extends RepositoryEvent>, EventHandlerMethod>();

    @Override
    public void onApplicationEvent(RepositoryEvent event) {

        Class<? extends RepositoryEvent> eventType = event.getClass();

        if (!handlerMethods.containsKey(eventType)) {
            return;
        }

        for (EventHandlerMethod handlerMethod : handlerMethods.get(eventType)) {

            Object src = event.getSource();

            if (!ClassUtils.isAssignable(handlerMethod.targetType, src.getClass())) {
                continue;
            }

            List<Object> parameters = new ArrayList<Object>();
            parameters.add(src);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking {} handler for {}.", event.getClass().getSimpleName(), event.getSource());
            }

            ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.handler, parameters.toArray());
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {

        Class<?> beanType = ClassUtils.getUserClass(bean);

        RepositoryEventHandler typeAnno = AnnotationUtils.findAnnotation(beanType, RepositoryEventHandler.class);

        if (typeAnno == null) {
            return bean;
        }

        ReflectionUtils.doWithMethods(beanType, method -> {
            inspect(bean, method, BeforeCreate.class, BeforeCreateEvent.class);
            inspect(bean, method, AfterCreate.class, AfterCreateEvent.class);
            inspect(bean, method, BeforeUpdate.class, BeforeUpdateEvent.class);
            inspect(bean, method, AfterUpdate.class, AfterUpdateEvent.class);
            inspect(bean, method, BeforeDelete.class, BeforeDeleteEvent.class);
            inspect(bean, method, AfterDelete.class, AfterDeleteEvent.class);
        }, Methods.USER_METHODS);

        return bean;
    }

    /**
     * Inspects the given handler method for an annotation of the given type. If the annotation present an
     * {@link EventHandlerMethod} is registered for the given {@link RepositoryEvent} type.
     *
     * @param handler        must not be {@literal null}.
     * @param method         must not be {@literal null}.
     * @param annotationType must not be {@literal null}.
     * @param eventType      must not be {@literal null}.
     */
    private <T extends Annotation> void inspect(Object handler, Method method, Class<T> annotationType,
                                                Class<? extends RepositoryEvent> eventType) {

        T annotation = AnnotationUtils.findAnnotation(method, annotationType);

        if (annotation == null) {
            return;
        }

        if (method.getParameterTypes().length == 0) {
            throw new IllegalStateException(String.format(PARAMETER_MISSING, method));
        }

        ResolvableType parameter = ResolvableType.forMethodParameter(method, 0, handler.getClass());
        EventHandlerMethod handlerMethod = EventHandlerMethod.of(parameter.resolve(), handler, method);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Annotated handler method found: {}", handlerMethod);
        }

        List<EventHandlerMethod> events = handlerMethods.get(eventType);

        if (events == null) {
            events = new ArrayList<>();
        }

        if (events.isEmpty()) {
            handlerMethods.add(eventType, handlerMethod);
            return;
        }

        events.add(handlerMethod);
        Collections.sort(events);
        handlerMethods.put(eventType, events);
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class EventHandlerMethod implements Comparable<EventHandlerMethod> {

        final Class<?> targetType;
        final Method method;
        final Object handler;

        public static EventHandlerMethod of(Class<?> targetType, Object handler, Method method) {

            ReflectionUtils.makeAccessible(method);
            return new EventHandlerMethod(targetType, method, handler);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(EventHandlerMethod o) {
            return AnnotationAwareOrderComparator.INSTANCE.compare(this.method, o.method);
        }
    }
}
