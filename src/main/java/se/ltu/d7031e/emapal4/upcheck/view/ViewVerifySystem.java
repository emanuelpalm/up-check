package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

/**
 * {@link View} useful for verifying UPPAAL system integrity.
 */
public interface ViewVerifySystem extends View {
    /**
     * @return UPPAAL system selection event publisher
     */
    EventPublisher<String> onSystemPath();

    /**
     * @return UPPAAL queries file selection event publisher
     */
    EventPublisher<String> onQueriesPath();

    /**
     * @return UPPAAL queries file save event publisher
     */
    EventPublisher<Queries> onQueriesSave();

    /**
     * @param pathString path to new currently selected UPPAAL system
     */
    void setSystemPath(final String pathString);

    /**
     * @param queries string containing UPPAAL queries
     */
    void setQueries(final String queries);

    /**
     * @param status     indication of validity of currently selected UPPAAL system
     * @param systemName name of system related to status
     */
    void setSystemStatus(final Status status, final String systemName);

    /**
     * @param pathString path to new currently selected UPPAAL queries file
     */
    void setQueriesPath(final String pathString);

    /**
     * @param status      indication of validity of currently selected UPPAAL queries file
     * @param queriesName name of queries file related to status
     */
    void setQueriesStatus(final Status status, final String queriesName);

    /**
     * @param report report to present
     */
    void setReport(final String report);

    /**
     * Indicates validity status of some selected UPPAAL system.
     */
    enum Status {
        NOT_FOUND,
        NOT_LOADED,
        NOT_PROVIDED,
        NOT_VALID,
        OK,
    }

    class Queries {
        private final String pathString;
        private final String data;

        Queries(final String pathString, final String data) {
            this.pathString = pathString;
            this.data = data;
        }

        public String pathString() {
            return pathString;
        }

        public String data() {
            return data;
        }
    }
}
