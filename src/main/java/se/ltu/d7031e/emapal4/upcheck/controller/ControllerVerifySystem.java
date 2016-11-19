package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxyException;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewVerifySystem;

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
        final Consumer<String> setSystemPath = pathString -> {
            try {
                uppaalProxy.setSystemByPath(pathString);

                UserData.setUppaalSystemPath(pathString);
                view.setSystemPath(pathString);
                view.setSystemStatus(ViewVerifySystem.SystemStatus.OK);

            } catch (final UppaalProxyException e) {
                switch (e.status()) {
                    case SYSTEM_NOT_FOUND:
                        view.setSystemStatus(ViewVerifySystem.SystemStatus.NOT_FOUND);
                        break;
                    case SYSTEM_NOT_VALID:
                        view.setSystemStatus(ViewVerifySystem.SystemStatus.NOT_VALID);
                        break;
                    case SYSTEM_NOT_PROVIDED:
                        view.setSystemStatus(ViewVerifySystem.SystemStatus.NOT_PROVIDED);
                        break;
                    default:
                        view.showException(e);
                }
            }
        };
        final String lastSystemPathString = UserData.uppaalSystemPath();
        if (lastSystemPathString != null && lastSystemPathString.length() > 0) {
            setSystemPath.accept(lastSystemPathString);
        }
        view.onUppaalSystemPath().subscribe(setSystemPath);
    }

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
