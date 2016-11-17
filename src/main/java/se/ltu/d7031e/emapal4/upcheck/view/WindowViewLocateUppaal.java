package se.ltu.d7031e.emapal4.upcheck.view;

import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * {@link WindowView} useful for locating a local UPPAAL installation.
 */
public class WindowViewLocateUppaal implements WindowView, ViewLocateUppaal {
    private final EventBroker<Path> onConfirmPath = new EventBroker<>();

    @Override
    public JPanel panel() throws Exception {
        return new JPanel(new BorderLayout()) {{
            setBackground(Styles.COLOR_BACKGROUND_PRIMARY);

            final JPanel root = this;
            final JTextField fieldPath = new JTextField() {{
                setBorder(Styles.BORDER_EMPTY_FIELD);
                setFont(Styles.FONT_PARAGRAPH);
            }};
            final JLabel labelStatus = new JLabel() {{
                setBorder(Styles.BORDER_EMPTY_FIELD);
                setFont(Styles.FONT_PARAGRAPH);
                setForeground(Styles.COLOR_ERROR);
                setMinimumSize(new Dimension(100, 80));
            }};

            final Supplier<Path> verifyAndGetPath = () -> {
                final String pathString = fieldPath.getText();
                if (pathString == null || pathString.trim().length() == 0) {
                    labelStatus.setText("Please provide a valid UPPAAL folder path.");
                    return null;
                }
                final Path path = new File(pathString).toPath();
                if (!Files.isDirectory(path)) {
                    labelStatus.setText("Path does not identity a folder.");
                    return null;
                }
                final Path pathLibJar = path.resolve("lib/model.jar");
                if (!Files.exists(pathLibJar) || !Files.isRegularFile(pathLibJar)) {
                    labelStatus.setText("Path does not identify an eligible UPPAAL folder.");
                    return null;
                }
                labelStatus.setText("");
                return path;
            };

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
                                SwingUtilities.invokeLater(verifyAndGetPath::get);
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
                    addActionListener(evt -> {
                        final Path path = verifyAndGetPath.get();
                        if (path != null) {
                            onConfirmPath.publish(path);
                        }
                    });
                }});
            }}, BorderLayout.PAGE_END);
        }};
    }

    @Override
    public boolean resizable() {
        return false;
    }

    @Override
    public EventPublisher<Path> onConfirmPath() {
        return onConfirmPath;
    }
}
