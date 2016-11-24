package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicException;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicFactory;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    private final DynamicFactory dynamicFactory;
    private final DynamicObject engine;

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
    }

    /**
     * Loads UPPAAL system from file at given path.
     *
     * @param pathString identifies local filesystem location where UPPAAL system is located
     * @return object useful for interacting with UPPAAL system
     * @throws UppaalProxyException Thrown if failing to resolve provided path
     */
    public UppaalSystem loadSystemAt(final String pathString) throws UppaalProxyException {
        try {
            final DynamicObject prototypeDocument = dynamicFactory.create("com.uppaal.model.core2.PrototypeDocument");
            final DynamicObject document = prototypeDocument.invoke("load", Paths.get(pathString).toUri().toURL());

            // UPPAAL version 4.0.x stores problems in a Vector, while version 4.1+ uses an ArrayList.
            List<?> problems = new ArrayList<>();
            DynamicObject uppaalSystem;
            try {
                uppaalSystem = engine.invoke("getSystem", document.unwrap(), problems);

            } catch (final DynamicException e) {
                if (!(e.getCause() instanceof NoSuchMethodException)) {
                    throw e;
                }
                problems = new Vector();
                uppaalSystem = engine.invoke("getSystem", document.unwrap(), problems);
            }
            if (problems.size() > 0) {
                throw new UppaalProxyException(UppaalProxyStatus.SYSTEM_NOT_VALID);
            }
            return new UppaalSystem(uppaalSystem);

        } catch (final MalformedURLException e) {
            throw new UppaalProxyException(UppaalProxyStatus.SYSTEM_NOT_FOUND, e);
        }
    }
}
