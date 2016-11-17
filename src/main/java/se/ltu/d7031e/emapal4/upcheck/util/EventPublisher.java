package se.ltu.d7031e.emapal4.upcheck.util;

import java.util.function.Consumer;

/**
 * Publishes event to registered subscribers.
 */
public interface EventPublisher<Event> {
    /**
     * @param subscriber Receiver of future published events.
     * @return Provided receiver.
     */
    Consumer<Event> subscribe(final Consumer<Event> subscriber);

    /**
     * @param subscriber Previously registered event receiver to stop receiving events.
     * @return If a subscriber was removed, {@code true}. In any other case, {@code false}.
     */
    boolean unsubscribe(final Consumer<Event> subscriber);
}
