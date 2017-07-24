package org.labun.springframework.data.repository.events.event;

/**
 * Emitted before an entity is deleted from the repository.
 * 
 * @author Jon Brisbin
 */
public class BeforeDeleteEvent extends RepositoryEvent {

	private static final long serialVersionUID = 9150212393209433211L;

	public BeforeDeleteEvent(Object source) {
		super(source);
	}
}
