package se.ltu.d7031e.emapal4.upcheck.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Helper class for loading various kinds of local resources.
 */
public class ResourceLoader {
    /**
     * Loads local image resource by given name.
     *
     * @param name name identifying image resource to load
     * @return loaded image
     */
    public static Image loadImage(final String name) {
        try {
            final InputStream imageStream = ResourceLoader.class.getClassLoader().getResourceAsStream(name);
            if (imageStream == null) {
                throw new IllegalArgumentException("No such image resource: " + name);
            }
            return ImageIO.read(imageStream);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
