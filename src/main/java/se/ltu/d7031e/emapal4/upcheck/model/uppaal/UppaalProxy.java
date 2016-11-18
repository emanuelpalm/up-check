package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    public UppaalProxy(final Path pathToLocalInstallation) throws UppaalPathInvalidException, IOException {
        final UppaalPathStatus pathStatus = UppaalPathStatus.validate(pathToLocalInstallation);
        if (pathStatus != UppaalPathStatus.OK) {
            throw new UppaalPathInvalidException(pathStatus);
        }
    }
}
