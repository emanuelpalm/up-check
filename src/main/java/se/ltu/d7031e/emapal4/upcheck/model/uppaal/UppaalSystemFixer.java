package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.system.UppaalSystem;
import se.ltu.d7031e.emapal4.upcheck.util.Chain;
import se.ltu.d7031e.emapal4.upcheck.util.Promise;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link UppaalSystem} fix suggester.
 * <p>
 * Uses a collection of {@link UppaalSystemFixerStrategy} objects to figure out how a given {@link UppaalSystem} might
 * be changed to make it conform to some {@link UppaalQueries}.
 * <p>
 * Note that the object is immutable. Any change to the object produces a modified shallow copy.
 */
public class UppaalSystemFixer {
    private final Context context;
    private final Duration timeout;
    private final Chain<UppaalSystemFixerStrategy> strategies;

    /**
     * Creates new UPPAAL system fixer.
     *
     * @param proxy   UPPAAL proxy object
     * @param system  system to be fixed
     * @param queries queries stating properties the fixed system must conform to
     */
    public UppaalSystemFixer(final UppaalProxy proxy, final UppaalSystem system, final UppaalQueries queries) {
        context = new Context(proxy, system, queries);
        timeout = Duration.ofSeconds(Long.MAX_VALUE);
        strategies = Chain.empty();
    }

    private UppaalSystemFixer(final Context context, final Duration timeout, final Chain<UppaalSystemFixerStrategy> strategies) {
        this.context = context;
        this.timeout = timeout;
        this.strategies = strategies;
    }

    /**
     * Adds UPPAAL system fixer strategy to list of employed strategies.
     *
     * @param strategy strategy to add
     * @return updated UPPAAL system fixer object
     */
    public UppaalSystemFixer addStrategy(final UppaalSystemFixerStrategy strategy) {
        return new UppaalSystemFixer(context, timeout, strategies.prepend(strategy));
    }

    /**
     * Sets deadline.
     *
     * @param timeout timeout duration
     * @return updated UPPAAL system fixer object
     * @see #runAsync()
     */
    public UppaalSystemFixer setTimeout(final Duration timeout) {
        return new UppaalSystemFixer(context, timeout, strategies);
    }

    /**
     * Starts evaluating strategies in new thread, returning promise of eventual result.
     * <p>
     * The thread will never run longer than any duration set using {@link #setTimeout(Duration)}. In case of
     * employing multiple {@link UppaalSystemFixerStrategy} objects, each is given an equal share of the time to
     * execute.
     *
     * @return promise of an eventual report
     */
    public Promise<UppaalSystemFixerReport> runAsync() {
        return new Promise<>(new Promise.Task<UppaalSystemFixerReport>() {
            private final AtomicBoolean isAborted = new AtomicBoolean(false);
            private final Object lock = this;

            private Thread thread = null;

            @Override
            public void execute(final Promise.OnResult<UppaalSystemFixerReport> onResult) throws Throwable {
                if (isAborted.get()) {
                    return;
                }
                final Runnable runnable = () -> {
                    final UppaalSystemFixerReport report = new UppaalSystemFixerReport();
                    final Duration strategyTimeout = timeout.dividedBy(strategies.size());
                    for (final UppaalSystemFixerStrategy strategy : strategies) {
                        if (isAborted.get()) {
                            return;
                        }
                        final Instant strategyDeadline = Instant.now().plus(strategyTimeout);
                        final List<UppaalSystemModification> modifications = strategy.apply(context.system, modifiedSystem -> {
                            try {
                                for (final UppaalQuery query : context.queries) {
                                    final UppaalQueryRequest request = context.proxy.request(modifiedSystem, query);
                                    request.onProgress().subscribe(nil -> {
                                        if (Instant.now().isAfter(strategyDeadline)) {
                                            throw new UppaalSystemFixerTimeoutException();
                                        }
                                    });
                                    final UppaalQueryResult result = request.submit();
                                    if (result.status() != UppaalQueryResult.Status.TRUE) {
                                        return false;
                                    }
                                }
                            } catch (final UppaalQueryException e) {
                                e.printStackTrace();
                                return false;
                            }
                            return true;
                        });
                        report.add(modifications);
                    }
                    onResult.onSuccess(report);
                };
                synchronized (lock) {
                    thread = new Thread(runnable);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.setDaemon(true);
                    thread.run();
                }
            }

            @Override
            public void abort() throws InterruptedException {
                if (isAborted.compareAndSet(false, true)) {
                    synchronized (lock) {
                        if (thread != null) {
                            thread.join();
                            thread = null;
                        }
                    }
                }
            }
        });
    }

    private static class Context {
        final UppaalProxy proxy;
        final UppaalSystem system;
        final UppaalQueries queries;

        private Context(final UppaalProxy proxy, final UppaalSystem system, final UppaalQueries queries) {
            this.proxy = proxy;
            this.system = system;
            this.queries = queries;
        }
    }
}
