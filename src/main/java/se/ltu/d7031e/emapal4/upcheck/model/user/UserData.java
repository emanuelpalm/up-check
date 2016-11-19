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
    private static final String KEY_SYSTEM_PATH = "SYSTEM_PATH";
    private static final String KEY_UPPAAL_PATH = "UPPAAL_PATH";

    /**
     * @return Path to last viewed UPPAAL system, if any.
     */
    public static String systemPath() {
        return UserProperties.get(KEY_SYSTEM_PATH);
    }

    /**
     * @return Path to local UPPAAL installation, if any known.
     */
    public static String uppaalPath() {
        return UserProperties.get(KEY_UPPAAL_PATH);
    }

    /**
     * @param pathString New last viewed UPPAAL system path to set.
     */
    public static void setSystemPath(final String pathString) {
        UserProperties.set(KEY_SYSTEM_PATH, pathString);
    }

    /**
     * @param pathString New UPPAAL path to set.
     */
    public static void setUppaalPath(final String pathString) {
        UserProperties.set(KEY_UPPAAL_PATH, pathString);
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
