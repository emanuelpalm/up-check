package se.ltu.dcc.upcheck.util;

import java.util.function.Consumer;

/**
 * Publishes event to registered subscribers.
 *
 * @see EventBroker
 * @see EventSubscription
 */
public interface EventPublisher<Event> {
    /**
     * @param subscriber receiver of subsequently published events
     * @return subscription handler
     */
    EventSubscription subscribe(final Consumer<Event> subscriber);
}
