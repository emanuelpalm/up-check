package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Holds and renders a single {@link WindowView}.
 */
public class Window {
    private final JFrame frame = new JFrame("UpCheck");

    /**
     * Creates new {@link Window} with given {@link View}.
     *
     * @param view Initial Window {@link View}.
     */
    public Window(final WindowView view) {
        setView(view);
    }

    /**
     * @param action Action executed in UI thread after window is closed.
     */
    public void setOnClosing(final Runnable action) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                SwingUtilities.invokeLater(action);
            }
        });
    }

    /**
     * Replaces any current {@link WindowView} with given.
     *
     * @param view View to set.
     */
    public void setView(final WindowView view) {
        SwingUtilities.invokeLater(() -> {
            try {
                {
                    final Container content = frame.getContentPane();
                    content.removeAll();
                    content.add(view.panel());
                }
                frame.pack();
                frame.setMinimumSize(frame.getSize());
                frame.setVisible(true);

            } catch (final Throwable e) {
                JOptionPane.showMessageDialog(frame, "Unexpected Application Error", e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        });
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
