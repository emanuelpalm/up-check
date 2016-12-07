package se.ltu.d7031e.emapal4.upcheck.model.user;

import se.ltu.d7031e.emapal4.upcheck.util.OsFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

/**
 * Keeps track of application user data.
 */
public class UserData {
    private static final String KEY_UPPAAL_FOLDER_ROOT = "UPPAAL_FOLDER_ROOT";
    private static final String KEY_UPPAAL_QUERIES_PATH = "UPPAAL_QUERIES_PATH";
    private static final String KEY_UPPAAL_SYSTEM_PATH = "UPPAAL_SYSTEM_PATH";

    /**
     * @return path to local UPPAAL installation, if any known
     */
    public static String uppaalFolderRoot() {
        return UserProperties.get(KEY_UPPAAL_FOLDER_ROOT);
    }

    /**
     * @return path to last viewed UPPAAL queries file, if any
     */
    public static String uppaalQueriesPath() {
        return UserProperties.get(KEY_UPPAAL_QUERIES_PATH);
    }

    /**
     * @return path to last viewed UPPAAL system, if any
     */
    public static String uppaalSystemPath() {
        return UserProperties.get(KEY_UPPAAL_SYSTEM_PATH);
    }

    /**
     * @param pathString new UPPAAL path to set
     */
    public static void setUppaalFolderRoot(final String pathString) {
        UserProperties.set(KEY_UPPAAL_FOLDER_ROOT, pathString);
    }

    /**
     * @param pathString new last viewed UPPAAL queries file path to set
     */
    public static void setUppaalQueriesPath(final String pathString) {
        UserProperties.set(KEY_UPPAAL_QUERIES_PATH, pathString);
    }

    /**
     * @param pathString new last viewed UPPAAL system path to set
     */
    public static void setUppaalSystemPath(final String pathString) {
        UserProperties.set(KEY_UPPAAL_SYSTEM_PATH, pathString);
    }

    private static class UserProperties {
        private static final Path path;

        private static String get(final String key) {
            final String pathString = loadProperties().getProperty(key);
            if (pathString != null) {
                return pathString;
            }
            return System.getenv("UPCHECK_" + key);
        }

        private static void set(final String key, final String value) {
            Objects.requireNonNull(key);
            final Properties properties = loadProperties();
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

        private static Properties loadProperties() {
            try {
                final Properties properties = new Properties();
                properties.loadFromXML(Files.newInputStream(path));
                return properties;

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
                if (!Files.exists(path)) {
                    Files.createDirectories(path.getParent());
                }
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
