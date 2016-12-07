package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.*;
import java.awt.*;

/**
 * Some dialog useful for displaying error messages.
 */
class WindowDialogError {
    private String title = null;
    private String message = null;
    private String details = null;
    private boolean isModal = false;

    /**
     * @param title error title to set
     */
    WindowDialogError title(final String title) {
        this.title = title;
        return this;
    }

    /**
     * @param message error message to set
     */
    WindowDialogError message(final String message) {
        this.message = message;
        return this;
    }

    /**
     * @param details error details to set
     */
    WindowDialogError details(final String details) {
        this.details = details;
        return this;
    }

    /**
     * @param isModal whether or not dialog is to be modal
     */
    WindowDialogError isModal(final boolean isModal) {
        this.isModal = isModal;
        return this;
    }

    /**
     * Causes dialog to be presented to application user.
     */
    void show() {
        final JDialog dialog = new JDialog((Frame) null, title != null ? title : "Error", isModal);
        {
            final JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            if (message != null) {
                final JLabel labelMessage = new JLabel("<html><p style=\"width: 368px\">" + message.replace("\n", "<br/>") + "</p></html>");
                labelMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
                labelMessage.setBorder(Styles.BORDER_EMPTY_MEDIUM);
                labelMessage.setFont(Styles.FONT_ERROR);
                labelMessage.setForeground(Styles.COLOR_FOREGROUND_SECONDARY);

                final JPanel panelMessage = new JPanel();
                panelMessage.setBackground(Styles.COLOR_ERROR);
                panelMessage.add(labelMessage);

                panel.add(panelMessage);
            }

            if (details != null) {
                final JLabel labelDetails = new JLabel(details);
                labelDetails.setBorder(Styles.BORDER_EMPTY_SMALL);
                labelDetails.setFont(Styles.FONT_SMALL);

                final JScrollPane scrollPaneDetails = new JScrollPane(labelDetails, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPaneDetails.setBorder(Styles.BORDER_EMPTY_MEDIUM);
                scrollPaneDetails.setPreferredSize(new Dimension(360, 180));

                panel.add(scrollPaneDetails);
            }

            {
                final JButton buttonOk = new JButton("OK");
                buttonOk.addActionListener(e -> dialog.dispose());
                buttonOk.setBorder(Styles.BORDER_EMPTY_FIELD);
                buttonOk.setFocusPainted(false);
                buttonOk.setFont(Styles.FONT_PARAGRAPH);

                final JPanel panelOk = new JPanel(new BorderLayout());
                panelOk.setBackground(Styles.COLOR_ERROR);
                panelOk.setBorder(Styles.BORDER_EMPTY_MEDIUM);
                panelOk.add(buttonOk, BorderLayout.EAST);

                panel.add(panelOk);
            }
            dialog.getContentPane().add(panel);
        }
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
