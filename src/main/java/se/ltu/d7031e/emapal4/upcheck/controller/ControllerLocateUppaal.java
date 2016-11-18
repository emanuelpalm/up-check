package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalPathStatus;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewLocateUppaal;

import java.nio.file.Paths;

/**
 * Controls interactions between {@link ViewLocateUppaal} instance and model.
 */
public class ControllerLocateUppaal implements Controller<ViewLocateUppaal> {
    @Override
    public void register(final Navigator navigator, final ViewLocateUppaal view) {
        view.onConfirmPath().subscribe(pathString -> {
            final ViewLocateUppaal.PathStatus pathStatus = verifyPath(pathString);
            view.setPathStatus(pathStatus);
            if (pathStatus == ViewLocateUppaal.PathStatus.OK) {
                UserData.setUppaalPath(pathString);
                try {
                    final UppaalProxy uppaalProxy = new UppaalProxy(Paths.get(pathString));
                    navigator.navigateTo(new ControllerVerifySystem(uppaalProxy));

                } catch (final Exception e) {
                    view.showException(e);
                }
            }
        });
        view.onVerifyPath().subscribe(pathString -> view.setPathStatus(verifyPath(pathString)));
    }

    private ViewLocateUppaal.PathStatus verifyPath(final String pathString) {
        final UppaalPathStatus pathStatus = UppaalPathStatus.validate(pathString);
        switch (pathStatus) {
            case NOT_A_DIRECTORY:
                return ViewLocateUppaal.PathStatus.NOT_A_DIRECTORY;
            case NOT_A_UPPAAL_DIRECTORY:
                return ViewLocateUppaal.PathStatus.NOT_A_UPPAAL_DIRECTORY;
            case NOT_PROVIDED:
                return ViewLocateUppaal.PathStatus.NOT_PROVIDED;
            case OK:
                return ViewLocateUppaal.PathStatus.OK;
        }
        throw new IllegalStateException("Unhandled UPPAAL path status: " + pathString);
    }

    @Override
    public Class<ViewLocateUppaal> viewClass() {
        return ViewLocateUppaal.class;
    }
}
