package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolder;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolderException;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolderStatus;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewLocateUppaal;

/**
 * Controls interactions between {@link ViewLocateUppaal} instance and model.
 */
public class ControllerLocateUppaal implements Controller<ViewLocateUppaal> {
    @Override
    public void register(final Navigator navigator, final ViewLocateUppaal view) {
        view.onConfirmPath().subscribe(pathString -> {
            try {
                final UppaalFolder uppaalFolder = UppaalFolder.create(pathString);
                final UppaalProxy uppaalProxy = new UppaalProxy(uppaalFolder);

                UserData.setUppaalFolderRoot(pathString);
                navigator.navigateTo(new ControllerVerifySystem(uppaalProxy));

            } catch (final UppaalFolderException e) {
                view.setPathStatus(toPathStatus(e.status()));

            } catch (final Exception e) {
                view.showException(null, e);
            }
        });
        view.onVerifyPath().subscribe(pathString -> {
            final UppaalFolderStatus status = UppaalFolder.validate(pathString);
            view.setPathStatus(toPathStatus(status));
        });
    }

    private ViewLocateUppaal.PathStatus toPathStatus(final UppaalFolderStatus status) {
        switch (status) {
            case NOT_A_DIRECTORY:
                return ViewLocateUppaal.PathStatus.NOT_A_DIRECTORY;
            case NOT_A_UPPAAL_DIRECTORY:
                return ViewLocateUppaal.PathStatus.NOT_A_UPPAAL_DIRECTORY;
            case NOT_PROVIDED:
                return ViewLocateUppaal.PathStatus.NOT_PROVIDED;
            case OK:
                return ViewLocateUppaal.PathStatus.OK;
        }
        throw new IllegalStateException("Unhandled UPPAAL path status: " + status);
    }

    @Override
    public Class<ViewLocateUppaal> viewClass() {
        return ViewLocateUppaal.class;
    }
}
