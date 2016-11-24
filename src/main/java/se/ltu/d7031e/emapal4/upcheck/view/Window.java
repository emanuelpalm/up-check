package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

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
                frame.setVisible(false);
                {
                    final Container content = frame.getContentPane();
                    content.removeAll();
                    content.add(view.panel());
                }
                frame.setMinimumSize(new Dimension(10, 10));
                frame.pack();
                frame.setMinimumSize(frame.getSize());
                frame.setResizable(view.resizable());
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (final Throwable e) {
                showException(null, e);
            }
        });
    }

    @Override
    public Class<WindowView> viewClass() {
        return WindowView.class;
    }

    /**
     * @param message Message to display to application user.
     * @param e Exception to log and display to application user.
     */
    @Override
    public void showException(final String message, final Throwable e) {
        showExceptionInternal(message, e);
    }

    static void showExceptionInternal(String message, final Throwable e) {
        e.printStackTrace();

        message = Optional
                .ofNullable(message)
                .orElse(Optional.ofNullable(e.getLocalizedMessage())
                        .orElse("An error of type " + e.getClass().getName() + " prevented the operation from completing."));

        final ByteArrayOutputStream stackTraceStream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stackTraceStream));

        new WindowDialogError()
                .title("Unexpected Application Error")
                .message(message)
                .details("<html><b>Details:<br /></b><pre>" + stackTraceStream.toString() + "</pre><html>")
                .isModal(true)
                .show();
    }

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
