package se.ltu.dcc.upcheck.controller;

import se.ltu.dcc.upcheck.model.uppaal.UppaalFolder;
import se.ltu.dcc.upcheck.model.uppaal.UppaalFolderException;
import se.ltu.dcc.upcheck.model.uppaal.UppaalFolderStatus;
import se.ltu.dcc.upcheck.model.user.UserData;
import se.ltu.dcc.upcheck.view.ViewLocateUppaal;

import java.util.function.Consumer;

/**
 * Controls interactions between {@link ViewLocateUppaal} instance and model.
 */
public class ControllerLocateUppaal implements Controller<ViewLocateUppaal> {
    private final Consumer<UppaalFolder> consumerUppaalFolder;

    /**
     * @param consumerUppaalFolder function executed when a valid UPPAAL folder has been selected
     */
    public ControllerLocateUppaal(final Consumer<UppaalFolder> consumerUppaalFolder) {
        this.consumerUppaalFolder = consumerUppaalFolder;
    }

    @Override
    public void register(final Navigator navigator, final ViewLocateUppaal view) {
        view.onConfirmPath().subscribe(pathString -> {
            try {
                final UppaalFolder uppaalFolder = UppaalFolder.create(pathString);
                UserData.setUppaalFolderRoot(pathString);
                navigator.navigateTo(null);
                consumerUppaalFolder.accept(uppaalFolder);

            } catch (final UppaalFolderException e) {
                view.setPathStatus(toPathStatus(e.status()));

            } catch (final Throwable e) {
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
