package se.ltu.d7031e.emapal4.upcheck.util;

/**
 * An exception occurring while using some {@link DynamicObject} or {@link DynamicMethod}.
 */
public class DynamicException extends RuntimeException {
    /**
     * @param cause the cause of this exception being created
     */
    public DynamicException(final Exception cause) {
        super(cause);
    }
}
