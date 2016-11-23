package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

/**
 * Renders some current {@link View}, causing it to be visible to the application user.
 */
public interface Renderer<V extends View> {
    /**
     * @return Renderer close event publisher.
     */
    EventPublisher<Void> onClose();

    /**
     * Replaces any current {@link View} with given.
     *
     * @param view View to set.
     */
    void setView(final V view);

    /**
     * @param message Error message to be displayed to application user.
     * @param e       Exception to be displayed to application user.
     */
    void showException(final String message, final Throwable e);

    /**
     * @return Rendered {@link View} class.
     */
    Class<V> viewClass();
}
