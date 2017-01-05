package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.QueryVerificationResult;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;
import se.ltu.d7031e.emapal4.upcheck.util.Lazy;
import se.ltu.d7031e.emapal4.upcheck.util.Promise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Builder used to assemble and {@link #submit()} UPPAAL query requests.
 */
public class UppaalQueryRequest {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private final Engine engine;
    private final UppaalSystem system;
    private final UppaalQuery query;

    private final Lazy<EventBroker<List<SymbolicTransition>>> onTrace = new Lazy<>(EventBroker::new);
    private final Lazy<EventBroker<Void>> onProgress = new Lazy<>(EventBroker::new);

    UppaalQueryRequest(final Engine engine, final UppaalSystem system, final UppaalQuery query) {
        this.engine = engine;
        this.system = system;
        this.query = query;
    }

    /**
     * @return query failure trace event publisher
     */
    public EventPublisher<List<SymbolicTransition>> onTrace() {
        return onTrace.value();
    }

    /**
     * @return query analysis progress event publisher
     */
    public EventPublisher<Void> onProgress() {
        return onProgress.value();
    }

    /**
     * Submits UPPAAL query request for evaluation.
     *
     * @return query result
     * @throws UppaalQueryException thrown in case of any anomaly
     */
    public Promise<UppaalQueryResult> submit() throws UppaalQueryException {
        return new Promise<>(new Promise.Task<UppaalQueryResult>() {
            @Override
            public void execute(final Promise.OnResult<UppaalQueryResult> onResult) {
                EXECUTOR.execute(() -> {
                    try {
                        final QueryVerificationResult result = engine.query(system, onTrace.isValueCreated() ? "trace 1" : "trace 0", query.data(), new QueryFeedback() {
                            public void appendText(final String s) {}

                            @Override
                            public void setCurrent(final int pos) {}

                            @Override
                            public void setTrace(final char result, final String feedback, final ArrayList<SymbolicTransition> trace, final int cycle, final QueryVerificationResult verificationResult) {
                                onTrace.ifValueCreated(eventBroker -> eventBroker.publish(trace));
                            }

                            @Override
                            public void setFeedback(final String feedback) {}

                            @Override
                            public void setLength(final int length) {}

                            @Override
                            public void setProgress(final int load, final long vm, final long rss, final long cached, final long avail, final long swap, final long swapfree, final long user, final long sys, final long timestamp) {
                                onProgress.ifValueCreated(eventBroker -> eventBroker.publish(null));
                            }

                            @Override
                            public void setProgressAvail(final boolean availability) {}

                            @Override
                            public void setResultText(final String s) {}

                            @Override
                            public void setSystemInfo(final long vmsize, final long physsize, final long swapsize) {}
                        });
                        if (result.exception != null) {
                            onResult.onFailure(new UppaalQueryException(result.exception));

                        } else {
                            onResult.onSuccess(new UppaalQueryResult(result.result));
                        }
                    } catch (final EngineException e) {
                        onResult.onFailure(new UppaalQueryException(e));
                    }
                });
            }

            @Override
            public void cancel() {
                engine.cancel();
                try {
                    engine.connect();

                } catch (final EngineException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
