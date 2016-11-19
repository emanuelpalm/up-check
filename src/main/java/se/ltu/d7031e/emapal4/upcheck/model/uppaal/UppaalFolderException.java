package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

/**
 * Indicates that some file system path of relevance does not refer to a valid UPPAAL installation.
 */
public class UppaalFolderException extends Exception {
    private final UppaalFolderStatus status;

    /**
     * Creates new exception with provided UPPAAL path validation status.
     */
    UppaalFolderException(final UppaalFolderStatus status) {
        this.status = status;
    }

    /**
     * @return UPPAAL path status.
     */
    public UppaalFolderStatus status() {
        return status;
    }
}
