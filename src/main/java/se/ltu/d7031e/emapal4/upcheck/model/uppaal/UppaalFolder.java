package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.Os;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Represents a local UPPAAL installation folder.
 */
public class UppaalFolder {
    private final Path binServerExe;
    private final Path[] jars;
    private final Path root;

    private UppaalFolder(final Path root) {
        this.root = root;

        binServerExe = new Os.Factory<Path>() {
            @Override
            public Path createOnLinux() {
                return root.resolve("bin-Linux/server");
            }

            @Override
            public Path createOnMacOsX() {
                return root.resolve("Contents/MacOS/server");
            }

            @Override
            public Path createOnWindows() {
                return root.resolve("bin-Win32\\server.exe");
            }
        }.create();
        try {
            jars = Files.find(root, 16, (path, attributes) -> {
                final String name = path.getFileName().toString();
                return name.endsWith(".jar") && !name.contains("-javadoc") && !name.contains("-source");
            }).toArray(Path[]::new);

        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Attempts to create new UPPAAL installation folder object.
     *
     * @param pathString path to installation folder root
     * @return UPPAAL folder object
     * @throws UppaalFolderException in case of provided folder being invalid
     */
    public static UppaalFolder create(final String pathString) throws UppaalFolderException {
        if (pathString == null || pathString.length() == 0) {
            throw new UppaalFolderException(UppaalFolderStatus.NOT_PROVIDED);
        }
        final Path path = Paths.get(pathString);
        if (!Files.isDirectory(path)) {
            throw new UppaalFolderException(UppaalFolderStatus.NOT_A_DIRECTORY);
        }
        final UppaalFolder uppaalFolder = new UppaalFolder(path);
        if (!uppaalFolder.containsRequiredFiles()) {
            throw new UppaalFolderException(UppaalFolderStatus.NOT_A_UPPAAL_DIRECTORY);
        }
        return uppaalFolder;
    }

    private boolean containsRequiredFiles() {
        return Stream.concat(Stream.of(binServerExe()), Arrays.stream(jars))
                .allMatch(Files::exists);
    }

    /**
     * Validates provided UPPAAL installation folder root path.
     *
     * @param pathString path to installation folder root
     * @return validity status of provided path
     */
    public static UppaalFolderStatus validate(final String pathString) {
        try {
            create(pathString);
            return UppaalFolderStatus.OK;

        } catch (final UppaalFolderException e) {
            return e.status();
        }
    }

    /**
     * Path to server executable.
     */
    public Path binServerExe() {
        return binServerExe;
    }

    /**
     * Path to UPPAAL JAR archives.
     */
    public Path[] jars() {
        return jars;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
