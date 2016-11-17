package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import java.nio.file.Path;

/**
 * {@link View} useful for locating a local UPPAAL installation.
 */
public interface ViewLocateUppaal extends View {
    /**
     * @return UPPAAL installation path confirmation event publisher.
     */
    EventPublisher<Path> onConfirmPath();
}
