package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

/**
 * Indicates that some file system path of relevance does not refer to a valid UPPAAL installation.
 */
public class UppaalPathInvalidException extends Exception {
    private final UppaalPathStatus status;

    /**
     * Creates new exception with provided UPPAAL path validation status.
     */
    UppaalPathInvalidException(final UppaalPathStatus status) {
        this.status = status;
    }

    /**
     * @return UPPAAL path status.
     */
    public UppaalPathStatus status() {
        return status;
    }
}
