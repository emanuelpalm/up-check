package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.Problem;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.system.UppaalSystem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    private static final Pattern REGEX_UPPAAL_VERSION = Pattern.compile("(\\d+).(\\d+).(\\d+)");

    private final Engine engine;

    /**
     * Creates new UPPAAL proxy object.
     *
     * @param uppaalFolder folder representing some local UPPAAL installation
     * @throws UppaalProxyException thrown if failing to to connect to local UPPAAL server
     */
    public UppaalProxy(final UppaalFolder uppaalFolder) throws UppaalProxyException {
        try {
            engine = new Engine();
            engine.setServerPath(uppaalFolder.binServerExe().toString());
            engine.connect();

            final String version = engine.getVersion();
            final Matcher matcher = REGEX_UPPAAL_VERSION.matcher(version);
            if (!matcher.find()) {
                throw new UppaalProxyException(UppaalProxyStatus.ENGINE_INCOMPATIBLE, "Unknown UPPAAL engine version:\n" + version);
            }
            final int major = Integer.parseInt(matcher.group(1));
            final int minor = Integer.parseInt(matcher.group(2));
            if (major == 4 && minor < 1 || major < 4) {
                throw new UppaalProxyException(UppaalProxyStatus.ENGINE_INCOMPATIBLE, "Incompatible UPPAAL engine version:\n" + version);
            }
        } catch (final LinkageError e) {
            throw new UppaalProxyException(UppaalProxyStatus.ENGINE_INCOMPATIBLE, e);

        } catch (final RuntimeException | IOException | EngineException e) {
            throw new UppaalProxyException(UppaalProxyStatus.ENGINE_NOT_CONNECTED, e);
        }
    }

    /**
     * Loads UPPAAL system from file at given path.
     *
     * @param pathString identifies local filesystem location where UPPAAL system is located
     * @return object useful for interacting with UPPAAL system
     * @throws UppaalProxyException thrown if failing to resolve provided path
     */
    public UppaalSystem loadSystemAt(final String pathString) throws Throwable {
        try {
            final PrototypeDocument prototypeDocument = new PrototypeDocument();
            final Document document = prototypeDocument.load(Paths.get(pathString).toUri().toURL());

            final ArrayList<Problem> problems = new ArrayList<>();
            final UppaalSystem uppaalSystem = engine.getSystem(document, problems);
            if (problems.size() > 0) {
                throw new UppaalProxyException(UppaalProxyStatus.SYSTEM_NOT_VALID, problems.toString());
            }
            return uppaalSystem;

        } catch (final MalformedURLException e) {
            throw new UppaalProxyException(UppaalProxyStatus.SYSTEM_NOT_FOUND, e);

        } catch (final Throwable e) {
            throw new UppaalProxyException(UppaalProxyStatus.ENGINE_INCOMPATIBLE, e);
        }
    }

    /**
     * Creates new request for given UPPAAL system to be analyzed using provided query.
     *
     * @param system UPPAAL system to analyze
     * @param query  target query
     * @return query result
     */
    public UppaalQueryRequest request(final UppaalSystem system, final UppaalQuery query) {
        return new UppaalQueryRequest(engine, system, query);
    }
}
