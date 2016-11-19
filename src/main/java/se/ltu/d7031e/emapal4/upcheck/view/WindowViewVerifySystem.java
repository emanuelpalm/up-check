package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

/**
 * {@link WindowView} useful for verifying UPPAAL system integrity.
 */
@SuppressWarnings("unused")
class WindowViewVerifySystem extends WindowView implements ViewVerifySystem {
    private final JFileChooser fileChooserUppaalSystem = new JFileChooser() {{
        final FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.isFile() && f.getName().endsWith(".xml"));
            }

            @Override
            public String getDescription() {
                return "UPPAAL System";
            }
        };
        addChoosableFileFilter(fileFilter);
        setFileFilter(fileFilter);
    }};
    private final JTextField fieldPath;
    private final JLabel labelStatus;
    private final JPanel root;

    private final EventBroker<String> onUppaalSystemPath = new EventBroker<>();

    public WindowViewVerifySystem() {
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

            add(new JPanel() {{
                setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                add(new JLabel("Verify UPPAAL System Integrity") {{
                    setAlignmentX(Component.LEFT_ALIGNMENT);
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setFont(Styles.FONT_HEADING1);
                    setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                }});
                add(new JLabel("Please provide a path to a local UPPAAL system.") {{
                    setAlignmentX(Component.LEFT_ALIGNMENT);
                    setBorder(Styles.BORDER_EMPTY_MEDIUM_NO_TOP);
                    setFont(Styles.FONT_PARAGRAPH);
                    setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                }});
                add(new JPanel(new GridLayout(0, 1)) {{
                    setAlignmentX(Component.LEFT_ALIGNMENT);
                    setBackground(Styles.COLOR_BACKGROUND_PRIMARY);
                    add(new JPanel() {{
                        setBorder(Styles.BORDER_EMPTY_MEDIUM);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        add(new JLabel("UPPAAL System") {{
                            setFont(Styles.FONT_PARAGRAPH);
                        }});
                        add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                        add(Box.createHorizontalGlue());
                        add(fieldPath);
                        add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                        add(new JButton("Open ...") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD);
                            setFocusPainted(false);
                            setFont(Styles.FONT_PARAGRAPH);
                            addActionListener(evt -> {
                                if (fileChooserUppaalSystem.showDialog(root, "Open System") == JFileChooser.APPROVE_OPTION) {
                                    fieldPath.setText(fileChooserUppaalSystem.getSelectedFile().getAbsolutePath());
                                    SwingUtilities.invokeLater(() -> onUppaalSystemPath.publish(fieldPath.getText()));
                                }
                                fileChooserUppaalSystem.setSelectedFile(null);
                            });
                        }});
                    }});
                    add(labelStatus);
                }});
            }}, BorderLayout.PAGE_START);
        }};
    }

    @Override
    public JPanel panel() {
        return root;
    }

    @Override
    public boolean resizable() {
        return true;
    }

    @Override
    public EventPublisher<String> onUppaalSystemPath() {
        return onUppaalSystemPath;
    }

    @Override
    public void setSystemPath(final String pathString) {
        if (pathString == null) {
            fieldPath.setText("");
        } else {
            final File file = new File(pathString);
            fieldPath.setText(file.getAbsolutePath());
            fileChooserUppaalSystem.setSelectedFile(file);
        }
    }

    @Override
    public void setSystemStatus(final SystemStatus status) {
        switch (status) {
            case NOT_FOUND:
                labelStatus.setText("Path does not identity an existing file.");
                break;
            case NOT_PROVIDED:
                labelStatus.setText("Please provide a valid UPPAAL system path.");
                break;
            case NOT_VALID:
                labelStatus.setText("Path does not identity a valid UPPAAL system.");
                break;
            case OK:
                labelStatus.setText("");
                break;
        }
    }
}
