package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import java.util.Objects;

/**
 * Maintains data about a single query.
 */
public class UppaalQuery {
    private final int lineNumber;
    private final String data;

    /**
     * Creates new UPPAAL query from given line number and data string.
     *
     * @param lineNumber source file line number
     * @param data       query data string
     */
    UppaalQuery(final int lineNumber, final String data) {
        this.lineNumber = lineNumber;
        this.data = data.trim();
    }

    /**
     * @return line number
     */
    public int lineNumber() {
        return lineNumber;
    }

    /**
     * @return query data string
     */
    public String data() {
        return data;
    }

    /**
     * @return whether or not the query data string is not empty
     */
    boolean isNotEmpty() {
        return data.length() != 0;
    }

    @Override
    public boolean equals(final Object other) {
        return other != null
                && other instanceof UppaalQuery
                && Objects.equals(data, ((UppaalQuery) other).data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}