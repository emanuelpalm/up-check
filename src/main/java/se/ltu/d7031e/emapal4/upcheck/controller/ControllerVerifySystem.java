package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.*;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewVerifySystem;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Controls interactions between {@link ViewVerifySystem} instance and model.
 */
public class ControllerVerifySystem implements Controller<ViewVerifySystem> {
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
        final UppaalQueries uppaalQueries = UppaalQueries.readString("");

        final Consumer<String> setSystemPath = pathString -> {
            final String name = Paths.get(pathString).getFileName().toString();
            try {
                atomicUppaalSystem.set(uppaalProxy.loadSystemAt(pathString));

                UserData.setUppaalSystemPath(pathString);
                view.setSystemPath(pathString);
                view.setSystemStatus(ViewVerifySystem.Status.OK, name);

            } catch (final UppaalProxyException e) {
                switch (e.status()) {
                    case ENGINE_NOT_CONNECTED:
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
        final Consumer<ViewVerifySystem.Queries> generateReport = queries -> uppaalQueries.update(queries.data());
        final Consumer<UppaalQuery> handleQuery = query -> {
            view.addReport("#> Query on line " + query.lineNumber() + " updated: " + query.data());
            final UppaalSystem uppaalSystem = atomicUppaalSystem.get();
            if (uppaalSystem == null) {
                view.addReport("!> No available UPPAAL system. Nothing to report.");
                return;
            }
            final UppaalQueryResult uppaalQueryResult = uppaalProxy.query(uppaalSystem, query.data());
            view.addReport(uppaalQueryResult.toString());
        };

        view.onSystemPath().subscribe(setSystemPath);
        view.onQueriesPath().subscribe(setQueriesPath);
        view.onQueriesSave().subscribe(saveQueries);
        view.onReportRequest().subscribe(generateReport);
        uppaalQueries.onQueryUpdated().subscribe(handleQuery);

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

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
