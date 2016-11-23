package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicException;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicFactory;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    private final DynamicFactory dynamicFactory;
    private final DynamicObject engine;
    private final UppaalFolder uppaalFolder;

    /**
     * Creates new UPPAAL proxy object.
     *
     * @param uppaalFolder folder representing some local UPPAAL installation
     * @throws UppaalProxyException Thrown if failing to to connect to local UPPAAL server.
     */
    public UppaalProxy(final UppaalFolder uppaalFolder) throws UppaalProxyException {
        try {
            dynamicFactory = new DynamicFactory(new URLClassLoader(new URL[]{
                    uppaalFolder.uppaalJar().toUri().toURL(),
                    uppaalFolder.libModelJar().toUri().toURL()
            }));
            this.engine = dynamicFactory.create("com.uppaal.engine.Engine");
            this.engine.invoke("setServerPath", uppaalFolder.binServerExe().toString());
            this.engine.invoke("connect");

        } catch (final Throwable e) {
            throw new UppaalProxyException(UppaalProxyStatus.ENGINE_NOT_CONNECTED, e);
        }
        this.uppaalFolder = uppaalFolder;
    }

    public void setSystemByPath(final String systemPathString) throws UppaalProxyException {
    }
}
