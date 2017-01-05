package se.ltu.d7031e.emapal4.upcheck.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A promise that some value will become available at some point in the future.
 *
 * @param <V> type of successful result
 */
public class Promise<V> {
    private final AtomicBoolean isExecuted = new AtomicBoolean(false);
    private final Task<V> task;

    /**
     * Creates new promise of eventual task resolution.
     *
     * @param task task to be resolved, eventually
     */
    public Promise(final Task<V> task) {
        this.task = task;
    }

    /**
     * Registers result handler.
     *
     * @param onResult handler to receive promise result
     * @return promise cancellation object
     */
    public Canceller then(final OnResult<V> onResult) {
        Objects.requireNonNull(onResult);
        if (isExecuted.compareAndSet(false, true)) {
            try {
                task.execute(onResult);

            } catch (final Throwable exception) {
                onResult.onFailure(exception);
            }
            return task::abort;
        }
        throw new IllegalStateException("Promise already executed.");
    }

    /**
     * A promise task, potentially executed asynchronously.
     *
     * @param <V> type of successful result
     */
    @FunctionalInterface
    public interface Task<V> {
        /**
         * Executes task.
         *
         * @param onResult handler to receive task result
         */
        void execute(final OnResult<V> onResult);

        /**
         * Aborts task, if the operation is supported.
         */
        default void abort() {
            throw new IllegalStateException("Task abortion not supported.");
        }
    }

    /**
     * A promise task result receiver.
     *
     * @param <V> type of successful result
     */
    public interface OnResult<V> extends OnSuccess<V>, OnFailure {}

    /**
     * A successful promise task result receiver.
     *
     * @param <V> type of successful result
     */
    @FunctionalInterface
    public interface OnSuccess<V> {
        void onSuccess(final V value);
    }

    /**
     * A erroneous promise task result receiver.
     */
    @FunctionalInterface
    public interface OnFailure {
        void onFailure(final Throwable exception);
    }

    /**
     * Used to cancel some running {@link Task}.
     */
    @FunctionalInterface
    public interface Canceller {
        /**
         * Signals desire for promise to never be resolved.
         *
         * @throws IllegalStateException if aborting is not supported by cancelled promise
         */
        void cancel();
    }
}
