package se.ltu.d7031e.emapal4.upcheck.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * An {@link EventPublisher} subscription.
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
