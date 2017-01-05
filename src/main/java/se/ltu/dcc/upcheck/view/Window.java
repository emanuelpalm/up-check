package se.ltu.dcc.upcheck.view;

import se.ltu.dcc.upcheck.util.EventBroker;
import se.ltu.dcc.upcheck.util.EventPublisher;
import se.ltu.dcc.upcheck.util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * Holds and renders a single {@link WindowView}.
 */
class Window implements Renderer<WindowView> {
    private final JFrame frame = new JFrame("UpCheck") {{
        setIconImages(Arrays.asList(
                ResourceLoader.loadImage("icon/icon_16x16.png"),
                ResourceLoader.loadImage("icon/icon_32x32.png"),
                ResourceLoader.loadImage("icon/icon_64x64.png"),
                ResourceLoader.loadImage("icon/icon_128x128.png"),
                ResourceLoader.loadImage("icon/icon_192x192.png"),
                ResourceLoader.loadImage("icon/icon_256x256.png")
        ));
    }};
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
                frame.setJMenuBar(new JMenuBar());
                {
                    final Container content = frame.getContentPane();
                    content.removeAll();
                    if (view == null) {
                        return;
                    }
                    content.add(view.panel());
                }
                view.menu().ifPresent(menu -> {
                    final JMenu menu0 = menu.visit(new JMenu(), new View.Menu.Visitor<JMenu>() {
                        @Override
                        public JMenu onCategory(final JMenu context, final View.Menu.Category category) {
                            final JMenu menu = new JMenu(category.name());
                            context.add(menu);
                            return menu;
                        }

                        @Override
                        public void onOption(final JMenu context, final View.Menu.Option option) {
                            final JMenuItem menuItem = new JMenuItem();
                            menuItem.setAction(new AbstractAction() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    option.action().run();
                                }
                            });
                            menuItem.setText(option.name());
                            context.add(menuItem);
                        }
                    });
                    final JMenuBar menuBar = new JMenuBar();
                    menuBar.add(menu0.getItem(0));
                    frame.setJMenuBar(menuBar);
                });
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
     * @param e       Exception to log and display to application user.
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

    @Override
    public void close() throws IOException {
        frame.dispose();
    }
}
