package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.core2.Document;
import com.uppaal.model.system.UppaalSystem;
import se.ltu.d7031e.emapal4.upcheck.util.Chain;
import se.ltu.d7031e.emapal4.upcheck.util.Promise;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UPPAAL {@link Document} fix suggester.
 * <p>
 * Uses a collection of {@link UppaalDocumentFixerStrategy} objects to figure out how a given {@link UppaalSystem} might
 * be changed to make it conform to some {@link UppaalQueries}.
 * <p>
 * Note that the object is immutable. Any change to the object produces a modified shallow copy.
 */
public class UppaalDocumentFixer {
    private final Context context;
    private final Duration timeout;
    private final Chain<UppaalDocumentFixerStrategy> strategies;

    /**
     * Creates new UPPAAL system fixer.
     *
     * @param proxy   UPPAAL proxy object
     * @param system  system to be fixed
     * @param queries queries stating properties the fixed system must conform to
     */
    public UppaalDocumentFixer(final UppaalProxy proxy, final UppaalSystem system, final UppaalQueries queries) {
        context = new Context(proxy, system, queries);
        timeout = Duration.ofSeconds(Long.MAX_VALUE);
        strategies = Chain.empty();
    }

    private UppaalDocumentFixer(final Context context, final Duration timeout, final Chain<UppaalDocumentFixerStrategy> strategies) {
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
    public UppaalDocumentFixer addStrategy(final UppaalDocumentFixerStrategy strategy) {
        return new UppaalDocumentFixer(context, timeout, strategies.prepend(strategy));
    }

    /**
     * Sets deadline.
     *
     * @param timeout timeout duration
     * @return updated UPPAAL system fixer object
     * @see #runAsync()
     */
    public UppaalDocumentFixer setTimeout(final Duration timeout) {
        return new UppaalDocumentFixer(context, timeout, strategies);
    }

    /**
     * Starts evaluating strategies in new thread, returning promise of eventual result.
     * <p>
     * The thread will never run longer than any duration set using {@link #setTimeout(Duration)}. In case of
     * employing multiple {@link UppaalDocumentFixerStrategy} objects, each is given an equal share of the time to
     * execute.
     *
     * @return promise of an eventual report
     */
    public Promise<UppaalDocumentFixerReport> runAsync() {
        return new Promise<>(new Promise.Task<UppaalDocumentFixerReport>() {
            private final AtomicBoolean isAborted = new AtomicBoolean(false);
            private final Object lock = this;

            private Thread thread = null;

            @Override
            public void execute(final Promise.OnResult<UppaalDocumentFixerReport> onResult) throws Throwable {
                if (isAborted.get()) {
                    return;
                }
                final Runnable runnable = () -> {
                    final UppaalDocumentFixerReport report = new UppaalDocumentFixerReport();
                    final Duration strategyTimeout = timeout.dividedBy(strategies.size());
                    for (final UppaalDocumentFixerStrategy strategy : strategies) {
                        if (isAborted.get()) {
                            return;
                        }
                        final Instant strategyDeadline = Instant.now().plus(strategyTimeout);
                        final List<UppaalDocumentFix> modifications = strategy.apply(context.system.getDocument(), modifiedDocument -> {
                            try {
                                for (final UppaalQuery query : context.queries) {
                                    final UppaalQueryRequest request = context.proxy.request(modifiedDocument, query);
                                    request.onProgress().subscribe(nil -> {
                                        if (Instant.now().isAfter(strategyDeadline)) {
                                            throw new UppaalDocumentFixerTimeoutException();
                                        }
                                    });
                                    final UppaalQueryResult result = request.submit();
                                    if (result.status() != UppaalQueryResult.Status.TRUE) {
                                        return false;
                                    }
                                }
                            } catch (final UppaalProxyException | UppaalQueryException e) {
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
