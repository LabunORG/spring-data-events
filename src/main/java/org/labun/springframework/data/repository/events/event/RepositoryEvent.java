package org.labun.springframework.data.repository.events.event;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract base class for events emitted on org.springframework.data.repository.CrudRepository data changing.
 * 
 * @author Jon Brisbin
 * @author Konstantin Labun
 */
public abstract class RepositoryEvent extends ApplicationEvent {

	private static final long serialVersionUID = -966689410815418259L;

	protected RepositoryEvent(Object source) {
		super(source);
	}
}
