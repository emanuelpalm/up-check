package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

/**
 * {@link View} useful for locating a local UPPAAL installation.
 */
public interface ViewLocateUppaal extends View {
    /**
     * @return UPPAAL installation path confirmation event publisher.
     */
    EventPublisher<String> onConfirmPath();

    /**
     * @return UPPAAL installation path verification event publisher.
     */
    EventPublisher<String> onVerifyPath();

    /**
     * @param status Indication of validity of currently selected UPPAAL installation path.
     */
    void setPathStatus(final PathStatus status);

    /**
     * Indicates validity status of some selected UPPAAL installation path.
     */
    enum PathStatus {
        NOT_DIRECTORY,
        NOT_PROVIDED,
        NOT_UPPAAL_DIRECTORY,
        OK,
    }
}
