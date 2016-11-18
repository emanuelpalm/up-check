package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Indicates validity status of some selected UPPAAL installation path.
 */
public enum UppaalPathStatus {
    NOT_A_DIRECTORY,
    NOT_A_UPPAAL_DIRECTORY,
    NOT_PROVIDED,
    OK;

    /**
     * Verifies that provided path refers to a local UPPAAL installation root folder.
     *
     * @param path Path to validate.
     * @return Validation status.
     */
    public static UppaalPathStatus validate(final Path path) {
        if (path == null || path.getParent() == null) {
            return NOT_PROVIDED;
        }
        if (!Files.isDirectory(path)) {
            return NOT_A_DIRECTORY;
        }
        try {
            if (!Files
                    .find(path, 2, (i, attrs) -> i
                            .toFile()
                            .getName()
                            .toLowerCase(Locale.ROOT)
                            .startsWith("server") && !attrs.isDirectory())
                    .findAny().isPresent()) {
                return NOT_A_UPPAAL_DIRECTORY;
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        final Path pathLibJar = path.resolve("lib/model.jar");
        if (!Files.exists(pathLibJar) || Files.isDirectory(pathLibJar)) {
            return NOT_A_UPPAAL_DIRECTORY;
        }
        return OK;
    }

    /**
     * Verifies that provided path refers to a local UPPAAL installation root folder.
     *
     * @param pathString Path to validate.
     * @return Validation status.
     */
    public static UppaalPathStatus validate(final String pathString) {
        if (pathString == null) {
            return NOT_PROVIDED;
        }
        return validate(Paths.get(pathString));
    }
}
