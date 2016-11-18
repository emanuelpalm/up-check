package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.ViewLocateUppaal;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
                    navigator.navigateTo(new ControllerVerifySystem());

                } catch (final ReflectiveOperationException e) {
                    view.showException(e);
                }
            }
        });
        view.onVerifyPath().subscribe(pathString -> view.setPathStatus(verifyPath(pathString)));
    }

    // TODO: Move to model.
    private ViewLocateUppaal.PathStatus verifyPath(final String pathString) {
        if (pathString == null || pathString.trim().length() == 0) {
            return ViewLocateUppaal.PathStatus.NOT_PROVIDED;
        }
        final Path path = new File(pathString).toPath();
        if (!Files.isDirectory(path)) {
            return ViewLocateUppaal.PathStatus.NOT_DIRECTORY;
        }
        final Path pathLibJar = path.resolve("lib/model.jar");
        if (!Files.exists(pathLibJar) || !Files.isRegularFile(pathLibJar)) {
            return ViewLocateUppaal.PathStatus.NOT_UPPAAL_DIRECTORY;
        }
        return ViewLocateUppaal.PathStatus.OK;
    }

    @Override
    public Class<ViewLocateUppaal> viewClass() {
        return ViewLocateUppaal.class;
    }
}
