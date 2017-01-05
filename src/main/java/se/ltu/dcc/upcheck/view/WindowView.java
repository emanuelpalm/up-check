package se.ltu.dcc.upcheck.view;

import javax.swing.*;

/**
 * A {@link View} that can be rendered by a {@link Window}.
 */
abstract class WindowView implements View {
    /**
     * @return Swing panel representing {@link View}.
     */
    abstract JPanel panel();

    /**
     * @return Whether or not the window may be resized.
     */
    boolean resizable() {
        return false;
    }

    @Override
    public void showException(final String message, final Throwable e) {
        Window.showExceptionInternal(message, e);
    }
}
