package se.ltu.dcc.upcheck.util;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Manages the publishing of events to registered subscribers.
 */
public class EventBroker<Event> implements EventPublisher<Event> {
    private ArrayList<Consumer<Event>> subscribers;

    /**
     * @param event Event to send to all registered subscribers.
     */
    public void publish(final Event event) {
        if (subscribers != null) {
            subscribers.forEach(subscriber -> subscriber.accept(event));
        }
    }

    @Override
    public EventSubscription subscribe(final Consumer<Event> subscriber) {
        if (subscribers == null) {
            subscribers = new ArrayList<>(1);
        }
        subscribers.add(subscriber);
        return () -> subscribers.remove(subscriber);
    }
}
