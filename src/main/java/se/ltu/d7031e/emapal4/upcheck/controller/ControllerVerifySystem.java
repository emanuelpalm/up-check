package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxyException;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalQueries;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalSystem;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewVerifySystem;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
        final Consumer<String> setSystemPath = pathString -> {
            final String name = Paths.get(pathString).getFileName().toString();
            try {
                atomicUppaalSystem.set(uppaalProxy.loadSystemAt(pathString));

                UserData.setUppaalSystemPath(pathString);
                view.setSystemPath(pathString);
                view.setSystemStatus(ViewVerifySystem.Status.OK, name);

                UserData.setUppaalQueriesPath(null);
                view.setQueries(null);
                view.setQueriesPath(null);
                view.setQueriesStatus(ViewVerifySystem.Status.OK, null);

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
            }
        };
        final String lastSystemPathString = UserData.uppaalSystemPath();
        if (lastSystemPathString != null && lastSystemPathString.length() > 0) {
            setSystemPath.accept(lastSystemPathString);
        }
        view.onSystemPath().subscribe(setSystemPath);

        final AtomicReference<UppaalQueries> atomicUppaalQueries = new AtomicReference<>();
        final Consumer<String> setQueriesPath = pathString -> {
            if (pathString == null || pathString.length() == 0) {
                view.setQueriesStatus(ViewVerifySystem.Status.NOT_PROVIDED, null);
                return;
            }
            final Path path = Paths.get(pathString);
            final String name = path.getFileName().toString();
            try {
                final UppaalQueries queries = UppaalQueries.readFile(path, StandardCharsets.UTF_8);
                atomicUppaalQueries.set(queries);

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
        final String lastQueriesPathString = UserData.uppaalQueriesPath();
        if (lastQueriesPathString != null && lastQueriesPathString.length() > 0) {
            setQueriesPath.accept(lastQueriesPathString);
        }
        view.onQueriesPath().subscribe(setQueriesPath);
    }

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
