package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

/**
 * {@link View} useful for verifying UPPAAL system integrity.
 */
public interface ViewVerifySystem extends View {
    /**
     * @return UPPAAL system selection event publisher.
     */
    EventPublisher<String> onUppaalSystemPath();

    /**
     * @param pathString Path to new currently selected UPPAAL system.
     */
    void setSystemPath(final String pathString);

    /**
     * @param status Indication of validity of currently selected UPPAAL system.
     */
    void setSystemStatus(final SystemStatus status);

    /**
     * Indicates validity status of some selected UPPAAL system.
     */
    enum SystemStatus {
        NOT_FOUND,
        NOT_PROVIDED,
        NOT_VALID,
        OK,
    }
}
