package org.labun.springframework.data.repository.events.event;

/**
 * Emitted before an entity is saved into the repository.
 *
 * @author Konstantin Labun
 */
public class BeforeUpdateEvent extends RepositoryEvent {

	private static final long serialVersionUID = -1404580942928384726L;

	public BeforeUpdateEvent(Object source) {
		super(source);
	}
}
