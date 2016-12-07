package se.ltu.d7031e.emapal4.upcheck.util;

import java.util.function.Consumer;

/**
 * Publishes event to registered subscribers.
 */
public interface EventPublisher<Event> {
    /**
     * @param subscriber receiver of subsequently published events
     * @return subscription handler
     */
    EventSubscription subscribe(final Consumer<Event> subscriber);
}
