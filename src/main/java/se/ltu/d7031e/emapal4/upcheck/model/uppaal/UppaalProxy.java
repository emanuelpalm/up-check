package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicException;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicFactory;
import se.ltu.d7031e.emapal4.upcheck.util.DynamicInterface;
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
            this.engine = dynamicFactory.newClassInstance("com.uppaal.engine.Engine");
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
            final DynamicObject prototypeDocument = dynamicFactory.newClassInstance("com.uppaal.model.core2.PrototypeDocument");
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

    /**
     * Analyzes given UPPAAL system using provided query.
     *
     * @param system UPPAAL system to analyze
     * @param query  target query
     * @return query result
     */
    public UppaalQueryResult query(final UppaalSystem system, final String query) {
        final DynamicInterface queryFeedback = dynamicFactory.newInterfaceInstance("com.uppaal.engine.QueryFeedback");
        final UppaalQueryResult queryResult = new UppaalQueryResult(queryFeedback);
        final Object unwrappedSystem = system.dynamicObject().unwrap();

        // TODO: Do something more useful with result.
        final char result = (char)engine
                .method("query", unwrappedSystem.getClass(), String.class, String.class, dynamicFactory.loadClass("com.uppaal.engine.QueryFeedback"))
                .invoke(unwrappedSystem, "trace 1", query, queryFeedback.unwrap());

        return queryResult;
    }
}
