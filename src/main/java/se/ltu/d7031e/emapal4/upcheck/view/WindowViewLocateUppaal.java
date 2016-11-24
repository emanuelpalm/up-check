package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;
import se.ltu.d7031e.emapal4.upcheck.util.OsFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link WindowView} useful for locating a local UPPAAL installation.
 */
@SuppressWarnings("unused")
class WindowViewLocateUppaal extends WindowView implements ViewLocateUppaal {
    private final JTextField fieldPath;
    private final JFileChooser fileChooser = new OsFactory<JFileChooser>() {
        @Override
        protected JFileChooser createOnMacOsX() {
            return new JFileChooser() {
                {
                    final FileFilter fileFilter = new FileFilter() {
                        @Override
                        public boolean accept(final File f) {
                            return f.isDirectory() || (f.isFile() && f.getName().endsWith(".app"));
                        }

                        @Override
                        public String getDescription() {
                            return "UPPAAL application (.app)";
                        }
                    };
                    addChoosableFileFilter(fileFilter);
                    setFileFilter(fileFilter);
                }

                @Override
                public void setSelectedFile(File file) {
                    if (file != null) {
                        final Path path = file.toPath();
                        if (!Files.exists(path)) {
                            final String name = file.toString();
                            try {
                                file = Files.find(path.getParent(), 1, (p, attr) -> p.toString().startsWith(name))
                                        .findFirst()
                                        .map(Path::toFile)
                                        .orElse(file);

                            } catch (final IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                    }
                    super.setSelectedFile(file);
                }
            };
        }

        @Override
        protected JFileChooser createOnOther() {
            return new JFileChooser() {{
                setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                resetChoosableFileFilters();
                addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(final File f) {
                        return f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Folder";
                    }
                });
                setAcceptAllFileFilterUsed(false);
            }};
        }
    }.create();

    private final JLabel labelStatus;
    private final JPanel root;

    private final EventBroker<String> onConfirmPath = new EventBroker<>();
    private final EventBroker<String> onVerifyPath = new EventBroker<>();

    WindowViewLocateUppaal() {
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
                setBackground(Styles.COLOR_ERROR);
                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                add(new JLabel("Locate UPPAAL Installation") {{
                    setAlignmentX(Component.LEFT_ALIGNMENT);
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setFont(Styles.FONT_HEADING1);
                    setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                }});
                add(new JLabel("<html>" +
                        "<p>In order for UpCheck to be functional, a local UPPAAL installation must be known.</p>" +
                        "<p>Please provide a path to a UPPAAL root folder.</p>" +
                        "</html>") {{
                    setAlignmentX(Component.LEFT_ALIGNMENT);
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
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setFocusPainted(false);
                    setFont(Styles.FONT_PARAGRAPH);
                    addActionListener(evt -> onConfirmPath.publish(fieldPath.getText()));
                }});
            }}, BorderLayout.PAGE_END);
        }};
    }

    @Override
    public JPanel panel() {
        return root;
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
            case NOT_A_DIRECTORY:
                labelStatus.setText("Path does not identity a folder.");
                break;
            case NOT_A_UPPAAL_DIRECTORY:
                labelStatus.setText("Path does not identify an eligible UPPAAL folder.");
                break;
            case OK:
                labelStatus.setText("");
                break;
        }
    }
}
