package se.ltu.dcc.upcheck.model.uppaal;

/**
 * Holds the result of some UPPAAL query.
 */
public class UppaalQueryResult {
    private final Status status;

    UppaalQueryResult(final char status) {
        this.status = Status.parse(status);
    }

    /**
     * @return UPPAAL query result validity status
     */
    public Status status() {
        return status;
    }

    /**
     * Denotes UPPAAL query result validity.
     */
    public enum Status {
        /**
         * Query is satisfied.
         */
        TRUE,

        /**
         * Query is not satisfied.
         */
        FALSE,

        /**
         * Query might be satisfied.
         */
        MAYBE;

        static Status parse(final char c) {
            switch (c) {
                case 'T':
                    return TRUE;
                case 'F':
                    return FALSE;
                case 'M':
                    return MAYBE;
                default:
                    throw new IllegalStateException("Unknown UPPAAL query result status: " + c);
            }
        }
    }
}
