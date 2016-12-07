package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.OsFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a local UPPAAL installation folder.
 */
public class UppaalFolder {
    private final Path binServerExe;
    private final Path libModelJar;
    private final Path root;
    private final Path uppaalJar;

    private UppaalFolder(final Path root) {
        this.root = root;

        binServerExe = new OsFactory<Path>() {
            @Override
            protected Path createOnLinux() {
                return root.resolve("bin-Linux/server");
            }

            @Override
            protected Path createOnMacOsX() {
                return root.resolve("Contents/MacOS/server");
            }

            @Override
            protected Path createOnWindows() {
                return root.resolve("bin-Win32\\server.exe");
            }
        }.create();
        libModelJar = new OsFactory<Path>() {
            @Override
            protected Path createOnLinux() {
                return root.resolve("lib/model.jar");
            }

            @Override
            protected Path createOnMacOsX() {
                return root.resolve("Contents/Java/model.jar");
            }

            @Override
            protected Path createOnWindows() {
                return root.resolve("lib\\model.jar");
            }
        }.create();
        uppaalJar = new OsFactory<Path>() {
            @Override
            protected Path createOnLinux() {
                return root.resolve("uppaal.jar");
            }

            @Override
            protected Path createOnMacOsX() {
                return root.resolve("Contents/Java/uppaal.jar");
            }

            @Override
            protected Path createOnWindows() {
                return root.resolve("uppaal.jar");
            }
        }.create();
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
        return Files.exists(binServerExe())
                && Files.exists(libModelJar())
                && Files.exists(uppaalJar());
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
     * Path to model JAR archive.
     */
    public Path libModelJar() {
        return libModelJar;
    }

    /**
     * Path to UPPAAL base JAR archive.
     */
    public Path uppaalJar() {
        return uppaalJar;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
