package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.JPanel;

/**
 * A {@link View} that can be rendered by a {@link Window}.
 */
interface WindowView extends View {
    /**
     * @return Swing panel representing {@link View}.
     */
    JPanel panel() throws Exception;
}
