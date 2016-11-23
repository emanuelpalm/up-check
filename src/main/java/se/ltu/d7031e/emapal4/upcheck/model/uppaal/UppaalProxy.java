package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicObject;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    private final ClassLoader classLoader;
    private final DynamicObject engine;
    private final UppaalFolder uppaalFolder;

    public UppaalProxy(final UppaalFolder uppaalFolder) throws UppaalProxyException {
        try {
            classLoader = new URLClassLoader(new URL[]{
                    uppaalFolder.uppaalJar().toUri().toURL(),
                    uppaalFolder.libModelJar().toUri().toURL()
            });
            this.engine = new DynamicObject(classLoader.loadClass("com.uppaal.engine.Engine").newInstance());
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
