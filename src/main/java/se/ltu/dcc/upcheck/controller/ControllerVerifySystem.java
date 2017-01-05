package se.ltu.dcc.upcheck.controller;

import com.uppaal.model.system.UppaalSystem;
import se.ltu.dcc.upcheck.Main;
import se.ltu.dcc.upcheck.model.user.UserData;
import se.ltu.dcc.upcheck.util.Promise;
import se.ltu.dcc.upcheck.util.Promises;
import se.ltu.dcc.upcheck.view.ViewVerifySystem;
import se.ltu.dcc.upcheck.model.uppaal.*;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Controls interactions between {@link ViewVerifySystem} instance and model.
 */
public class ControllerVerifySystem implements Controller<ViewVerifySystem> {
    private final AtomicReference<Promise.Receipt> atomicReportGenerationCanceller = new AtomicReference<>();
    private final UppaalProxy uppaalProxy;

    /**
     * @param uppaalProxy Local UPPAAL installation proxy used to perform verifications.
     */
    public ControllerVerifySystem(final UppaalProxy uppaalProxy) {
        this.uppaalProxy = uppaalProxy;
    }

    @Override
    public void register(final Navigator navigator, final ViewVerifySystem view) {
        final AtomicReference<UppaalSystem> atomicUppaalSystem = new AtomicReference<>();
        final UppaalQueries uppaalQueries = new UppaalQueries();

        final Consumer<String> setSystemPath = pathString -> {
            final String name = Paths.get(pathString).getFileName().toString();
            try {
                atomicUppaalSystem.set(uppaalProxy.loadSystemAt(pathString));

                UserData.setUppaalSystemPath(pathString);
                view.setSystemPath(pathString);
                view.setSystemStatus(ViewVerifySystem.Status.OK, name);

            } catch (final UppaalProxyException e) {
                switch (e.status()) {
                    case ENGINE_INCOMPATIBLE:
                        view.showException("It seems like the used UPPAAL engine isn't compatible with UpCheck.", e);
                        resetAndReboot();
                        break;
                    case ENGINE_ERROR:
                        view.showException("Failed to connect to UPPAAL engine.", e.getCause());
                        break;
                    case SYSTEM_NOT_FOUND:
                        view.setSystemStatus(ViewVerifySystem.Status.NOT_FOUND, name);
                        break;
                    case SYSTEM_NOT_VALID:
                        view.setSystemStatus(ViewVerifySystem.Status.NOT_VALID, name);
                        break;
                    case SYSTEM_NOT_PROVIDED:
                        view.setSystemStatus(ViewVerifySystem.Status.NOT_PROVIDED, name);
                        break;
                    default:
                        view.showException(null, e);
                }
            } catch (final UncheckedIOException e) {
                view.setSystemStatus(ViewVerifySystem.Status.NOT_LOADED, name);

            } catch (final Throwable e) {
                view.showException(null, e);

            } finally {
                UserData.setUppaalQueriesPath(null);
                view.setQueries(null);
                view.setQueriesPath(null);
                view.setQueriesStatus(ViewVerifySystem.Status.OK, null);
            }
        };
        final Consumer<String> setQueriesPath = pathString -> {
            if (pathString == null || pathString.length() == 0) {
                view.setQueriesStatus(ViewVerifySystem.Status.NOT_PROVIDED, null);
                return;
            }
            final Path path = Paths.get(pathString);
            final String name = path.getFileName().toString();
            try {
                final UppaalQueries queries = UppaalQueries.readFile(path, StandardCharsets.UTF_8);

                UserData.setUppaalQueriesPath(pathString);
                view.setQueries(queries.toString());
                view.setQueriesPath(pathString);
                view.setQueriesStatus(ViewVerifySystem.Status.OK, name);

            } catch (final UncheckedIOException e) {
                view.setQueriesStatus(ViewVerifySystem.Status.NOT_LOADED, name);

            } catch (final Throwable e) {
                view.showException(null, e);
            }
        };
        final Consumer<ViewVerifySystem.Queries> saveQueries = queries -> {
            try {
                Files.write(
                        Paths.get(queries.pathString()),
                        queries.data().getBytes(StandardCharsets.UTF_8));

            } catch (final Throwable e) {
                view.showException(null, e);
            }
        };
        final Consumer<ViewVerifySystem.Queries> generateReport = queries -> {
            view.addReport("=> Generating new UPPAAL report ...\r\n");
            uppaalQueries.update(queries.data());
        };
        final Consumer<Void> cancelReportGeneration = nil -> cancelReportGeneration();
        final Consumer<Void> reselectUppaalInstallationFolder = nil -> resetAndReboot();

        view.onSystemPath().subscribe(setSystemPath);
        view.onQueriesPath().subscribe(setQueriesPath);
        view.onQueriesSave().subscribe(saveQueries);
        view.onReportRequest().subscribe(generateReport);
        view.onReportRequestCanceled().subscribe(cancelReportGeneration);
        view.onMenuUppaalSelectInstallation().subscribe(reselectUppaalInstallationFolder);

        uppaalQueries.onUpdated()
                .subscribe(queries -> {
                    try {
                        final UppaalSystem uppaalSystem = atomicUppaalSystem.get();
                        if (uppaalSystem == null) {
                            view.addReport("!> No available UPPAAL system. Nothing to report.\r\n");
                            return;
                        }
                        final List<Promise<UppaalQueryResult.Status>> requestPromises = queries.asStream()
                                .map(query -> {
                                    try {
                                        final UppaalQueryRequest request = uppaalProxy.request(uppaalSystem, query);
                                        return Promise.cancellableOf(() -> view.addReport("#> Executing query: " + query))
                                                .thenAwait(request.submit())
                                                .thenMap(requestResult -> {
                                                    final UppaalQueryResult.Status status = requestResult.status();
                                                    view.addReport("   Result: " + status + "\r\n");
                                                    return status;
                                                });
                                    } catch (final UppaalQueryException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .collect(Collectors.toList());

                        final Promise.Receipt receipt = Promises.await(requestPromises)
                                .thenFilter(results -> {
                                    final boolean isSuggestingFixes = results.contains(UppaalQueryResult.Status.FALSE);
                                    if (!isSuggestingFixes) {
                                        view.addReport("!> All queries satisfied.\r\n");
                                    }
                                    return isSuggestingFixes;
                                })
                                .thenFlatMap(ignored -> {
                                    final Duration timeout = Duration.ofSeconds(10);
                                    view.addReport("#> Attempting to find valid system fixes for at most " + timeout.getSeconds() + " seconds ...");
                                    return new UppaalDocumentFixer(uppaalProxy, uppaalSystem, uppaalQueries)
                                            .addStrategy(new UppaalDocumentFixerStrategyRemoveEdges())
                                            .setTimeout(timeout)
                                            .execute();
                                })
                                .then(new Promise.OnResult<UppaalDocumentFixerReport>() {
                                    @Override
                                    public void onSuccess(final UppaalDocumentFixerReport fixerReport) {
                                        if (fixerReport.isEmpty()) {
                                            view.addReport("!> No possible fixes could be determined.\r\n");
                                        } else {
                                            view.addReport("!> The following changes satisfy all system queries:");
                                            for (final UppaalDocumentFix fix : fixerReport) {
                                                view.addReport("   -> " + fix);
                                            }
                                            view.addReport("");
                                            view.setQueriesStatus(ViewVerifySystem.Status.OK, null);
                                        }
                                    }

                                    @Override
                                    public void onFailure(final Throwable exception) {
                                        exception.printStackTrace();
                                        view.addReport("!> " + exception.getLocalizedMessage() + "\r\n");
                                        view.setQueriesStatus(ViewVerifySystem.Status.OK, null);
                                    }
                                })
                                .onCancel(() -> {
                                    view.addReport("!> Report generation cancelled.\r\n");
                                    view.setQueriesStatus(ViewVerifySystem.Status.OK, null);
                                });

                        view.setQueriesStatus(ViewVerifySystem.Status.PENDING, null);
                        atomicReportGenerationCanceller.set(receipt);

                    } catch (final RuntimeException e) {
                        final Throwable cause = e.getCause();
                        if (cause != null && cause instanceof UppaalQueryException) {
                            e.printStackTrace();
                            view.addReport("!> Report generation failed. Reason: " + e.getLocalizedMessage() + "\r\n");

                        } else {
                            throw e;
                        }
                    } catch (final Throwable e) {
                        view.showException(null, e);
                    }
                });

        // Initialize.
        {
            final String lastSystemPathString = UserData.uppaalSystemPath();
            final String lastQueriesPathString = UserData.uppaalQueriesPath();

            if (lastSystemPathString != null && lastSystemPathString.length() > 0) {
                setSystemPath.accept(lastSystemPathString);
            }
            if (lastQueriesPathString != null && lastQueriesPathString.length() > 0) {
                setQueriesPath.accept(lastQueriesPathString);
            }
        }
    }

    private void cancelReportGeneration() {
        final Promise.Receipt receipt = atomicReportGenerationCanceller.getAndSet(null);
        if (receipt != null) {
            receipt.cancel();
        }
    }

    @Override
    public void unregister() {
        cancelReportGeneration();
    }

    private void resetAndReboot() {
        UserData.setUppaalFolderRoot(null);
        Main.reboot();
    }

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
