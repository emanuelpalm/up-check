package se.ltu.dcc.upcheck.model.uppaal;

/**
 * Signifies an exceptional scenario encountered while evaluating some UPPAAL query request.
 */
public class UppaalQueryException extends Exception {
    UppaalQueryException(final Exception e) {
        super(e);
    }
}
