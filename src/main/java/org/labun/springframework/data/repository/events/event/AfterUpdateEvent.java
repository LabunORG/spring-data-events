package org.labun.springframework.data.repository.events.event;

/**
 * Emitted after a save to the repository.
 * 
 * @author Jon Brisbin
 * @author Konstantin Labun
 */
public class AfterUpdateEvent extends RepositoryEvent {

	private static final long serialVersionUID = 8568843338617401903L;

	public AfterUpdateEvent(Object source) {
		super(source);
	}
}
