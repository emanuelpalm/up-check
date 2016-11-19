package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

/**
 * Mediates communication between a local UPPAAL installation and this application.
 */
public class UppaalProxy {
    private final UppaalFolder uppaalFolder;

    public UppaalProxy(final UppaalFolder uppaalFolder) {
        this.uppaalFolder = uppaalFolder;
    }

    public void setSystemByPath(final String systemPathString) throws UppaalProxyException {
    }
}
