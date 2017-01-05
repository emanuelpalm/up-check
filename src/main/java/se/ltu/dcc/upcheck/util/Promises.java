package se.ltu.dcc.upcheck.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Various {@code Promise} utilities.
 *
 * @see Promise
 */
public class Promises {
    private Promises() {}

    /**
     * Waits for all given promises to complete successfully, and then yields their results in a list.
     * <p>
     * The yielded list of results correspond in order with the provided list of promises.
     * <p>
     * If any one of the given promises would fail, the returned promise is failed with only that exception.
     * <p>
     * Not that the provided promises are only executed in parallel if their tasks are all executed on different
     * threads. If executed on the current thread, or if a single-threaded executor is used, all promises are executed
     * in-order.
     *
     * @param promises promises to await
     * @param <V>      promise result type
     * @return promise of list of values
     */
    public static <V> Promise<List<V>> await(final List<Promise<V>> promises) {
        final AtomicBoolean isDone = new AtomicBoolean(false);
        final AtomicReference<ArrayList<Promise.Receipt>> atomicCancellers = new AtomicReference<>(new ArrayList<>(promises.size()));

        return new Promise<>(new Promise.Task<List<V>>() {
            @Override
            public void execute(final Promise.OnResult<List<V>> onResult) {
                final ArrayList<V> results = new ArrayList<>(promises.size());
                for (int i = promises.size(); i-- != 0; ) {
                    results.add(null);
                }
                final ArrayList<Promise.Receipt> receipts = new ArrayList<>(promises.size());
                for (int i = promises.size(); i-- != 0; ) {
                    receipts.add(null);
                }
                final AtomicInteger promiseCounter = new AtomicInteger(promises.size());
                int promiseIndex = 0;
                for (final Promise<V> promise : promises) {
                    final int index = promiseIndex++;

                    if (isDone.get()) {
                        return;
                    }

                    receipts.set(index, promise.then(new Promise.OnResult<V>() {
                        @Override
                        public void onSuccess(final V value) {
                            if (isDone.get()) {
                                return;
                            }

                            results.set(index, value);

                            if (promiseCounter.decrementAndGet() == 0) {
                                isDone.set(true);
                                onResult.onSuccess(results);
                            }
                        }

                        @Override
                        public void onFailure(final Throwable exception) {
                            if (isDone.compareAndSet(false, true)) {
                                onResult.onFailure(exception);
                            }
                        }
                    }));
                }
                atomicCancellers.set(receipts);
            }

            @Override
            public void cancel() {
                if (isDone.compareAndSet(false, true)) {
                    final ArrayList<Promise.Receipt> receipts = atomicCancellers.get();
                    if (receipts != null) {
                        receipts.forEach(Promise.Receipt::cancel);
                    }
                }
            }
        });
    }
}
