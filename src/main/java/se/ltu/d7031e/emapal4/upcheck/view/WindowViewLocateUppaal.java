package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

/**
 * {@link WindowView} useful for locating a local UPPAAL installation.
 */
class WindowViewLocateUppaal implements WindowView, ViewLocateUppaal {
    private final JTextField fieldPath;
    private final JLabel labelStatus;
    private final JPanel root;

    private final EventBroker<String> onConfirmPath = new EventBroker<>();
    private final EventBroker<String> onVerifyPath = new EventBroker<>();

    public WindowViewLocateUppaal() {
        fieldPath = new JTextField() {{
            setBorder(Styles.BORDER_EMPTY_FIELD);
            setFont(Styles.FONT_PARAGRAPH);
        }};

        labelStatus = new JLabel() {{
            setBorder(Styles.BORDER_EMPTY_MEDIUM);
            setFont(Styles.FONT_PARAGRAPH);
            setForeground(Styles.COLOR_ERROR);
            setMinimumSize(new Dimension(100, 80));
        }};

        root = new JPanel(new BorderLayout()) {{
            setBackground(Styles.COLOR_BACKGROUND_PRIMARY);

            final JPanel root = this;

            add(new JPanel(new GridLayout(0, 1)) {{
                setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                add(new JLabel("Locate UPPAAL Installation") {{
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setFont(Styles.FONT_HEADING1);
                    setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                }});
                add(new JLabel("<html>" +
                        "<p>In order for UpCheck to be functional, a local UPPAAL installation must be known.</p>" +
                        "<p>Please provide a path to a UPPAAL root folder.</p>" +
                        "</html>") {{
                    setBorder(Styles.BORDER_EMPTY_MEDIUM_NO_TOP);
                    setFont(Styles.FONT_PARAGRAPH);
                    setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                }});
            }}, BorderLayout.PAGE_START);
            add(new JPanel(new GridLayout(0, 1)) {{
                setBackground(Styles.COLOR_BACKGROUND_PRIMARY);
                add(new JPanel() {{
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                    add(new JLabel("Path") {{
                        setBorder(Styles.BORDER_EMPTY_FIELD);
                        setFont(Styles.FONT_PARAGRAPH);
                    }});
                    add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                    add(Box.createHorizontalGlue());
                    add(fieldPath);
                    add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                    add(new JButton("Select ...") {{
                        setBorder(Styles.BORDER_EMPTY_FIELD);
                        setFocusPainted(false);
                        setFont(Styles.FONT_PARAGRAPH);
                        addActionListener(evt -> {
                            final JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            fileChooser.resetChoosableFileFilters();
                            fileChooser.addChoosableFileFilter(new FileFilter() {
                                @Override
                                public boolean accept(final File f) {
                                    return f.isDirectory();
                                }

                                @Override
                                public String getDescription() {
                                    return "Folder";
                                }
                            });
                            fileChooser.setAcceptAllFileFilterUsed(false);

                            if (fileChooser.showDialog(root, "Select") == JFileChooser.APPROVE_OPTION) {
                                fieldPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                                SwingUtilities.invokeLater(() -> onVerifyPath.publish(fieldPath.getText()));
                            }
                            fileChooser.setSelectedFile(null);
                        });
                    }});
                }});
                add(labelStatus);
            }}, BorderLayout.CENTER);
            add(new JPanel() {{
                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                setBorder(Styles.BORDER_EMPTY_MEDIUM);
                add(Box.createHorizontalGlue());
                add(new JButton("Confirm") {{
                    setBorder(Styles.BORDER_EMPTY_FIELD);
                    setFocusPainted(false);
                    setFont(Styles.FONT_PARAGRAPH);
                    addActionListener(evt -> onConfirmPath.publish(fieldPath.getText()));
                }});
            }}, BorderLayout.PAGE_END);
        }};
    }

    @Override
    public JPanel panel() throws Exception {
        return root;
    }

    @Override
    public boolean resizable() {
        return false;
    }

    @Override
    public EventPublisher<String> onConfirmPath() {
        return onConfirmPath;
    }

    @Override
    public EventPublisher<String> onVerifyPath() {
        return onVerifyPath;
    }

    @Override
    public void setPathStatus(final PathStatus status) {
        switch (status) {
            case NOT_PROVIDED:
                labelStatus.setText("Please provide a valid UPPAAL folder path.");
                break;
            case NOT_DIRECTORY:
                labelStatus.setText("Path does not identity a folder.");
                break;
            case NOT_UPPAAL_DIRECTORY:
                labelStatus.setText("Path does not identify an eligible UPPAAL folder.");
                break;
            case OK:
                labelStatus.setText("");
                break;
        }
    }
}
