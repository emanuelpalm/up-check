package se.ltu.d7031e.emapal4.upcheck.util;

/**
 * A promise that some value will become available at some point in the future.
 *
 * @param <V> type of successful result
 */
public class Promise<V> {
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
     */
    public void then(final OnResult<V> onResult) {
        try {
            task.execute(onResult);

        } catch (final Throwable exception) {
            //noinspection unchecked
            onResult.onFailure(exception);
        }
    }

    /**
     * Registers result handlers.
     *
     * @param onSuccess handler to receive successful promise result
     * @param onFailure handler to receive erroneous promise result
     */
    public void then(final OnSuccess<V> onSuccess, final OnFailure onFailure) {
        then(new OnResult<V>() {
            @Override
            public void onSuccess(final V value) {
                onSuccess.onSuccess(value);
            }

            @Override
            public void onFailure(final Throwable exception) {
                onFailure.onFailure(exception);
            }

        });
    }

    /**
     * A promise task, potentially executed asynchronously.
     *
     * @param <V> type of successful result
     */
    @FunctionalInterface
    public interface Task<V> {
        void execute(final OnResult<V> onResult) throws Throwable;
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
}
