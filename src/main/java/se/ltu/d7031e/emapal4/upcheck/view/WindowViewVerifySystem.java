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

    private final JPanel root;
    private final EventBroker<String> onUppaalSystemPath = new EventBroker<>();

    private JLabel labelSystemStatus;
    private JLabel labelQueriesStatus;
    private JLabel labelReportStatus;
    private JTextArea textAreaQueries;
    private JTextArea textAreaReport;

    public WindowViewVerifySystem() {
        root = new JPanel(new BorderLayout()) {{
            setBackground(Styles.COLOR_BACKGROUND_PRIMARY);

            final JPanel root = this;

            add(new JPanel() {{
                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                add(new JPanel() {{
                    setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                    setBorder(Styles.BORDER_EMPTY_SMALL);
                    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                    add(new JLabel("UPPAAL System") {{
                        setAlignmentX(Component.LEFT_ALIGNMENT);
                        setFont(Styles.FONT_SMALL);
                        setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                    }});
                    add(Box.createHorizontalGlue());
                    add(new JButton("Select ...") {{
                        setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                        setFocusPainted(false);
                        setFont(Styles.FONT_SMALL);
                        addActionListener(evt -> {
                            if (fileChooserUppaalSystem.showDialog(root, "Select System") == JFileChooser.APPROVE_OPTION) {
                                final String selectedPath = fileChooserUppaalSystem.getSelectedFile().getAbsolutePath();
                                SwingUtilities.invokeLater(() -> onUppaalSystemPath.publish(selectedPath));
                            }
                            fileChooserUppaalSystem.setSelectedFile(null);
                        });
                    }});
                }});
                add(new JPanel() {{
                    setBorder(Styles.BORDER_EMPTY_SMALL);
                    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                    setOpaque(false);
                    add(labelSystemStatus = new JLabel() {{
                        setFont(Styles.FONT_SMALL_BOLD);
                        setText(" ");
                    }});
                    add(Box.createHorizontalGlue());
                }});
            }}, BorderLayout.NORTH);
            add(new JPanel(new GridLayout(0, 1)) {{
                add(new JPanel() {{
                    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                    add(new JPanel() {{
                        setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                        setBorder(Styles.BORDER_EMPTY_SMALL);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) getPreferredSize().getHeight()));
                        add(new JLabel("System Queries") {{
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                            setFont(Styles.FONT_SMALL);
                            setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                        }});
                        add(Box.createHorizontalGlue());
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(new JButton("Load ...") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(new JButton("Save") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(new JButton("Save as ...") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                        }});
                    }});
                    add(new JPanel() {{
                        setBorder(Styles.BORDER_EMPTY_SMALL);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        setOpaque(false);
                        add(labelQueriesStatus = new JLabel() {{
                            setFont(Styles.FONT_SMALL_BOLD);
                            setText(" ");
                        }});
                        add(Box.createHorizontalGlue());
                    }});
                    add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        add(new JScrollPane(textAreaQueries = new JTextArea() {{
                            setBorder(Styles.BORDER_EMPTY_SMALL);
                            setEnabled(false);
                            setEditable(false);
                            setLineWrap(true);
                            setFont(Styles.FONT_SMALL);
                            setRows(8);
                        }}, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {{
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                        }});
                    }});
                }});
                add(new JPanel() {{
                    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
                    add(new JPanel() {{
                        setBackground(Styles.COLOR_BACKGROUND_SECONDARY);
                        setBorder(Styles.BORDER_EMPTY_SMALL);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) getPreferredSize().getHeight()));
                        add(new JLabel("System Report") {{
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                            setFont(Styles.FONT_SMALL);
                            setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                        }});
                        add(Box.createHorizontalGlue());
                        add(new JButton("Generate") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                        }});
                    }});
                    add(new JPanel() {{
                        setBorder(Styles.BORDER_EMPTY_SMALL);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        setOpaque(false);
                        add(labelReportStatus = new JLabel() {{
                            setFont(Styles.FONT_SMALL_BOLD);
                            setText(" ");
                        }});
                        add(Box.createHorizontalGlue());
                    }});
                    add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        add(new JScrollPane(textAreaReport = new JTextArea() {{
                            setBorder(Styles.BORDER_EMPTY_SMALL);
                            setEditable(false);
                            setEnabled(false);
                            setLineWrap(true);
                            setFont(Styles.FONT_SMALL);
                            setRows(8);
                        }}, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {{
                            setAlignmentX(Component.LEFT_ALIGNMENT);
                        }});
                    }});
                }});
            }}, BorderLayout.CENTER);
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
        if (pathString != null) {
            fileChooserUppaalSystem.setSelectedFile(new File(pathString));
        }
    }

    @Override
    public void setSystemStatus(final SystemStatus status, final String systemName) {
        switch (status) {
            case NOT_FOUND:
                labelSystemStatus.setForeground(Styles.COLOR_ERROR);
                labelSystemStatus.setText("Path does not identity an existing file.");
                break;
            case NOT_PROVIDED:
                labelSystemStatus.setForeground(Styles.COLOR_ERROR);
                labelSystemStatus.setText("Please provide a valid UPPAAL system path.");
                break;
            case NOT_VALID:
                labelSystemStatus.setForeground(Styles.COLOR_ERROR);
                labelSystemStatus.setText("Path does not identity a valid UPPAAL system.");
                break;
            case OK:
                labelSystemStatus.setForeground(Styles.COLOR_FOREGROUND_PRIMARY);
                labelSystemStatus.setText(systemName + " ");
                break;
        }
    }
}
