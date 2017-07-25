package org.labun.springframework.data.repository.events;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.repository.support.Repositories;

/**
 * AutoConfiguration for spring events.
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = SpringEventsAutoConfiguration.class)
public class SpringEventsAutoConfiguration {
    @Bean
    public BeanPostProcessor repositoryEventHandlerAnnotationBeanPostProcessor() {
        return new AnnotatedEventHandlerInvoker();
    }

    @ConditionalOnMissingClass
    @Bean
    public Repositories repositories(ApplicationContext context) {
        return new Repositories(context);
    }
}
