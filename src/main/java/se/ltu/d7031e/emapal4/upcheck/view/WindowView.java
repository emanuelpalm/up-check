package se.ltu.d7031e.emapal4.upcheck.view;

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
    public void showException(final Throwable e) {
        Window.showException(e);
    }
}
