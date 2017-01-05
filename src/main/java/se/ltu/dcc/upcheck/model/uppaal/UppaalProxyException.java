package se.ltu.dcc.upcheck.model.uppaal;

/**
 * Indicates that some UPPAAL proxy operation failed to complete due to some exceptional scenario.
 */
public class UppaalProxyException extends Exception {
    private final UppaalProxyStatus status;

    /**
     * Creates new exception with provided UPPAAL proxy status and cause.
     */
    UppaalProxyException(final UppaalProxyStatus status, final Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * Creates new exception with provided UPPAAL proxy status and message.
     */
    UppaalProxyException(final UppaalProxyStatus status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * @return UPPAAL proxy status
     */
    public UppaalProxyStatus status() {
        return status;
    }
}
