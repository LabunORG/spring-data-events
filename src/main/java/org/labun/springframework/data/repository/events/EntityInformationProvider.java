package org.labun.springframework.data.repository.events;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Caching EntityInformation provider.
 */
@Service
public class EntityInformationProvider {
    private final Map<Class<?>, EntityInformation<?, ?>> entityInfoMap;
    private final Repositories repositories;

    public EntityInformationProvider(Repositories repositories) {
        this.repositories = repositories;
        this.entityInfoMap = new HashMap<>();
    }

    public EntityInformation getEntityInformationFor(Class entityType) {
        return entityInfoMap.computeIfAbsent(entityType, repositories::getEntityInformationFor);
    }
}
