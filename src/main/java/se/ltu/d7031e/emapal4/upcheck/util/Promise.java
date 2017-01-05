package se.ltu.d7031e.emapal4.upcheck.util;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

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
     * Creates new cancellable promise of given runnable.
     * <p>
     * Any promise cancel requests are ignored.
     *
     * @param runnable to be executed when promise is awaited
     * @return new promise
     */
    public static Promise<Void> cancellableOf(final Runnable runnable) {
        return new Promise<>(new Task<Void>() {
            @Override
            public void execute(OnResult<Void> onResult) {
                runnable.run();
                onResult.onSuccess(null);
            }

            @Override
            public void cancel() {}
        });
    }

    /**
     * Awaits promise result, blocking current thread until it becomes available.
     *
     * @return if successful, promise result
     * @throws Throwable if unsuccessful, promise exception
     */
    public V await() throws Throwable {
        final Semaphore semaphore = new Semaphore(0);
        final AtomicReference<V> atomicValue = new AtomicReference<>(null);
        final AtomicReference<Throwable> atomicException = new AtomicReference<>(null);

        then(new OnResult<V>() {
            @Override
            public void onSuccess(final V value) {
                atomicValue.set(value);
                semaphore.release();
            }

            @Override
            public void onFailure(final Throwable exception) {
                atomicException.set(exception);
                semaphore.release();
            }
        });
        semaphore.acquire();

        final Throwable exception = atomicException.get();
        if (exception != null) {
            throw exception;
        }
        return atomicValue.get();
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
            return task::cancel;
        }
        throw new IllegalStateException("Promise already executed.");
    }

    /**
     * Registers another promise to be executed after this one finishes.
     * <p>
     * The result of this promise is ignored if successful. Exceptions are propagated as usual.
     *
     * @param promise promise to be executed after this
     * @param <W>     provided promise result type
     * @return new promise
     */
    public <W> Promise<W> thenAwait(final Promise<W> promise) {
        Objects.requireNonNull(promise);
        return new Promise<>(new Task<W>() {
            @Override
            public void execute(final OnResult<W> onResult) {
                then(new OnResult<V>() {
                    @Override
                    public void onSuccess(final V value) {
                        promise.then(onResult);
                    }

                    @Override
                    public void onFailure(final Throwable exception) {
                        onResult.onFailure(exception);
                    }
                });
            }

            @Override
            public void cancel() {
                task.cancel();
                promise.task.cancel();
            }
        });
    }

    /**
     * Registers promise result filter.
     * <p>
     * If this promise is successfully evaluated, then the returned promise is provided only if it satisfies provided
     * predicate function.
     *
     * @param predicate determines if some promise result is satisfied
     * @return new promise
     */
    public Promise<Optional<V>> thenFilter(final Predicate<V> predicate) {
        Objects.requireNonNull(predicate);
        return new Promise<>(new Task<Optional<V>>() {
            @Override
            public void execute(final OnResult<Optional<V>> onResult) {
                then(new OnResult<V>() {
                    @Override
                    public void onSuccess(final V value) {
                        onResult.onSuccess(predicate.test(value) ? Optional.of(value) : Optional.empty());
                    }

                    @Override
                    public void onFailure(final Throwable exception) {
                        onResult.onFailure(exception);
                    }
                });
            }

            @Override
            public void cancel() {
                task.cancel();
            }
        });
    }

    /**
     * Registers flattened promise result transformer.
     * <p>
     * If this promise is successfully evaluated, then the result is mapped to the provided transformation function.
     * The return value of that transformation function is then unwrapped and provided to the returned promise.
     *
     * @param transformer function transforming successful promise result
     * @param <W>         transformer return type
     * @return new promise
     */
    public <W> Promise<W> thenFlatMap(final Function<V, Promise<W>> transformer) {
        Objects.requireNonNull(transformer);
        return new Promise<>(new Task<W>() {
            @Override
            public void execute(final OnResult<W> onResult) {
                then(new OnResult<V>() {
                    @Override
                    public void onSuccess(final V value) {
                        final Promise<W> promise = transformer.apply(value);
                        promise.then(new OnResult<W>() {
                            @Override
                            public void onSuccess(final W value) {
                                onResult.onSuccess(value);
                            }

                            @Override
                            public void onFailure(final Throwable exception) {
                                onResult.onFailure(exception);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Throwable exception) {
                        onResult.onFailure(exception);
                    }
                });
            }

            @Override
            public void cancel() {
                task.cancel();
            }
        });
    }

    /**
     * Registers promise result transformer.
     * <p>
     * If this promise is successfully evaluated, then the result is mapped to the provided transformation function.
     *
     * @param transformer function transforming successful promise result
     * @param <W>         transformer return type
     * @return new promise
     */
    public <W> Promise<W> thenMap(final Function<V, W> transformer) {
        Objects.requireNonNull(transformer);
        return new Promise<>(new Task<W>() {
            @Override
            public void execute(final OnResult<W> onResult) {
                then(new OnResult<V>() {
                    @Override
                    public void onSuccess(final V value) {
                        onResult.onSuccess(transformer.apply(value));
                    }

                    @Override
                    public void onFailure(final Throwable exception) {
                        onResult.onFailure(exception);
                    }
                });
            }

            @Override
            public void cancel() {
                task.cancel();
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
        /**
         * Executes task.
         *
         * @param onResult handler to receive task result
         */
        void execute(final OnResult<V> onResult);

        /**
         * Cancels task, if the operation is supported.
         */
        default void cancel() {
            throw new IllegalStateException("Promise task cancellation not supported.");
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

        /**
         * Creates new canceller that runs provided {@code runnable} before invoking this {@link #cancel()}.
         *
         * @param runnable to be executed before this {@link Canceller}
         * @return new canceller
         */
        default Canceller onCancel(final Runnable runnable) {
            return () -> {
                runnable.run();
                cancel();
            };
        }
    }
}
