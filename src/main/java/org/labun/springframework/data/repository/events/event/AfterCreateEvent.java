package org.labun.springframework.data.repository.events.event;

/**
 * Event that is emitted after a new entity is saved.
 * 
 * @author Jon Brisbin
 */
public class AfterCreateEvent extends RepositoryEvent {

	private static final long serialVersionUID = -7673953693485678403L;

	public AfterCreateEvent(Object source) {
		super(source);
	}
}
