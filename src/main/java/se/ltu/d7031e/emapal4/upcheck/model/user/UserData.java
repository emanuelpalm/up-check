package se.ltu.d7031e.emapal4.upcheck.model.user;

import se.ltu.d7031e.emapal4.upcheck.util.OsFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

/**
 * Keeps track of application user data.
 */
public class UserData {
    private static final String KEY_UPPAAL_SYSTEM_PATH = "UPPAAL_SYSTEM_PATH";
    private static final String KEY_UPPAAL_FOLDER_ROOT = "UPPAAL_FOLDER_ROOT";

    /**
     * @return Path to last viewed UPPAAL system, if any.
     */
    public static String uppaalSystemPath() {
        return UserProperties.get(KEY_UPPAAL_SYSTEM_PATH);
    }

    /**
     * @return Path to local UPPAAL installation, if any known.
     */
    public static String uppaalFolderRoot() {
        return UserProperties.get(KEY_UPPAAL_FOLDER_ROOT);
    }

    /**
     * @param pathString New last viewed UPPAAL system path to set.
     */
    public static void setUppaalSystemPath(final String pathString) {
        UserProperties.set(KEY_UPPAAL_SYSTEM_PATH, pathString);
    }

    /**
     * @param pathString New UPPAAL path to set.
     */
    public static void setUppaalFolderRoot(final String pathString) {
        UserProperties.set(KEY_UPPAAL_FOLDER_ROOT, pathString);
    }

    private static class UserProperties {
        private static final Path path;
        private static final Properties properties = new Properties();

        private static String get(final String key) {
            final String pathString = properties.getProperty(key);
            if (pathString != null) {
                return pathString;
            }
            return System.getenv("UPCHECK_" + key);
        }

        private static void set(final String key, final String value) {
            Objects.requireNonNull(key);
            if (value == null) {
                properties.setProperty(key, "");
            } else {
                properties.setProperty(key, value);
            }
            try {
                properties.storeToXML(Files.newOutputStream(path), LocalDateTime.now().toString());

            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        static {
            path = new OsFactory<Path>() {
                @Override
                protected Path createOnMacOsX() {
                    return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "UpCheck", "configuration.xml");
                }

                @Override
                protected Path createOnWindows() {
                    return Paths.get(System.getenv("AppData"), "UpCheck", "configuration.xml");
                }

                @Override
                protected Path createOnOther() {
                    return Paths.get(System.getProperty("user.home"), ".upcheckrc");
                }
            }.create();
            try {
                if (Files.exists(path)) {
                    properties.loadFromXML(Files.newInputStream(path));
                } else {
                    Files.createDirectories(path.getParent());
                }
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
