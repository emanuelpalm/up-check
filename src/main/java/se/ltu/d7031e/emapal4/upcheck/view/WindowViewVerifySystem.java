package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.*;

/**
 * {@link WindowView} useful for verifying UPPAAL system integrity.
 */
@SuppressWarnings("unused")
class WindowViewVerifySystem extends WindowView implements ViewVerifySystem {
    @Override
    public JPanel panel() {
        return new JPanel();
    }

    @Override
    public boolean resizable() {
        return true;
    }
}
