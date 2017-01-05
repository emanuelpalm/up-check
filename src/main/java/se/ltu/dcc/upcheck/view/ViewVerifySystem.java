package se.ltu.dcc.upcheck.view;

import se.ltu.dcc.upcheck.util.EventPublisher;

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
     * @return UPPAAL system report clear event publisher
     */
    EventPublisher<Void> onReportCleared();

    /**
     * @return UPPAAL system report request event publisher
     */
    EventPublisher<Queries> onReportRequest();

    /**
     * @return UPPAAL system report request cancellation event publisher
     */
    EventPublisher<Void> onReportRequestCanceled();

    /**
     * @return UPPAAL folder selection request event publisher
     */
    EventPublisher<Void> onMenuUppaalSelectInstallation();

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
    void addReport(final String report);

    /**
     * Indicates validity status of some selected UPPAAL system.
     */
    enum Status {
        NOT_FOUND,
        NOT_LOADED,
        NOT_PROVIDED,
        NOT_VALID,
        OK,
        PENDING,
    }

    /**
     * Represents a set of UPPAAL queries.
     */
    class Queries {
        private final String pathString;
        private final String data;

        /**
         * Creates new UPPAAL queries
         *
         * @param pathString path to file containing queries
         * @param data query data string
         */
        Queries(final String pathString, final String data) {
            this.pathString = pathString;
            this.data = data;
        }

        /**
         * @return path to file containing UPPAAL queries
         */
        public String pathString() {
            return pathString;
        }

        /**
         * @return string containing UPPAAL queries
         */
        public String data() {
            return data;
        }
    }
}
