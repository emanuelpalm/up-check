package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Holds and renders a single {@link WindowView}.
 */
class Window implements Renderer<WindowView> {
    private final JFrame frame = new JFrame("UpCheck");
    private final EventBroker<Void> onClose = new EventBroker<>();

    /**
     * Creates new {@link Window}.
     */
    Window() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                SwingUtilities.invokeLater(() -> onClose.publish(null));
            }
        });
    }

    @Override
    public EventPublisher<Void> onClose() {
        return onClose;
    }

     @Override
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
                frame.setResizable(view.resizable());
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (final Throwable e) {
                JOptionPane.showMessageDialog(frame, "Unexpected Application Error", e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Class<WindowView> viewClass() {
        return WindowView.class;
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
