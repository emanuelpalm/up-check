package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
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
        final Consumer<String> setSystemPath = path -> {
            // TODO: Check integrity of path.
            System.out.println(path);
            UserData.setSystemPath(path);
            view.setSystemPath(path);
            view.setSystemStatus(ViewVerifySystem.SystemStatus.OK);
        };
        final String lastSystemPathString = UserData.systemPath();
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
