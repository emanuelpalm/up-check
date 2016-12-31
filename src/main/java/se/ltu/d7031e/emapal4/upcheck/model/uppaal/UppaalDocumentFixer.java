package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.core2.Document;
import com.uppaal.model.system.UppaalSystem;
import se.ltu.d7031e.emapal4.upcheck.util.Chain;
import se.ltu.d7031e.emapal4.upcheck.util.Promise;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
     * @see #execute(ExecutorService)
     */
    public UppaalDocumentFixer setTimeout(final Duration timeout) {
        return new UppaalDocumentFixer(context, timeout, strategies);
    }

    /**
     * Starts evaluating strategies using provided {@code executorService}, returning promise of eventual result.
     * <p>
     * Evaluation will never run longer than a duration set using {@link #setTimeout(Duration)}. In case of employing
     * multiple {@link UppaalDocumentFixerStrategy} objects, each is given an equal share of the time to execute.
     *
     * @param executorService evaluation executor service
     * @return promise of an eventual report
     */
    public Promise<UppaalDocumentFixerReport> execute(final ExecutorService executorService) {
        return new Promise<>(new Promise.Task<UppaalDocumentFixerReport>() {
            private final AtomicBoolean isAborted = new AtomicBoolean(false);
            private final AtomicReference<Future<?>> atomicFuture = new AtomicReference<>();

            @Override
            public void execute(final Promise.OnResult<UppaalDocumentFixerReport> onResult) {
                if (isAborted.get()) {
                    return;
                }
                atomicFuture.set(executorService.submit(() -> {
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
                }));
            }

            @Override
            public void abort() {
                if (isAborted.compareAndSet(false, true)) {
                    final Future<?> future = atomicFuture.get();
                    if (future != null) {
                        future.cancel(true);
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
