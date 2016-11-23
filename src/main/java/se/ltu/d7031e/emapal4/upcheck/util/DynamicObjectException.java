package se.ltu.d7031e.emapal4.upcheck.util;

/**
 * An exception occurring while using some {@link DynamicObject}.
 */
public class DynamicObjectException extends RuntimeException {
    /**
     * @param cause the cause of this exception being created
     */
    public DynamicObjectException(final Exception cause) {
        super(cause);
    }
}
