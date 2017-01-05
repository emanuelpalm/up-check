package se.ltu.dcc.upcheck.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * An {@link EventPublisher} subscription.
 *
 * @see EventBroker
 * @see EventPublisher
 */
@FunctionalInterface
public interface EventSubscription extends Closeable {
    /**
     * Cancels subscription.
     */
    default void cancel() {
        try {
            close();
        } catch (final IOException e) { /* Ignore. */ }
    }
}
