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
    private final JFileChooser fileChooserSystem = new JFileChooser() {{
        final FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.isFile() && f.getName().endsWith(".xml"));
            }

            @Override
            public String getDescription() {
                return "UPPAAL System (.xml)";
            }
        };
        addChoosableFileFilter(fileFilter);
        setFileFilter(fileFilter);
    }};
    private final JFileChooser fileChooserQueries = new JFileChooser() {{
        final FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || (f.isFile() && f.getName().endsWith(".q"));
            }

            @Override
            public String getDescription() {
                return "UPPAAL Queries File (.q)";
            }
        };
        addChoosableFileFilter(fileFilter);
        setFileFilter(fileFilter);
    }};

    private final JPanel root;
    private final EventBroker<String> onSystemPath = new EventBroker<>();
    private final EventBroker<String> onQueriesPath = new EventBroker<>();
    private final EventBroker<String> onQueriesSave = new EventBroker<>();

    private JLabel labelSystemStatus;

    private JButton buttonQueriesLoad;
    private JButton buttonQueriesSave;
    private JButton buttonQueriesSaveAs;
    private JLabel labelQueriesStatus;
    private JTextArea textAreaQueries;

    private JButton buttonReportGenerate;
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
                            if (fileChooserSystem.showDialog(root, "Select System") == JFileChooser.APPROVE_OPTION) {
                                final String selectedPath = fileChooserSystem.getSelectedFile().getAbsolutePath();
                                SwingUtilities.invokeLater(() -> onSystemPath.publish(selectedPath));
                            }
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
                        add(buttonQueriesLoad = new JButton("Load ...") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                            addActionListener(evt -> {
                                if (fileChooserQueries.showDialog(root, "Load Queries") == JFileChooser.APPROVE_OPTION) {
                                    final String selectedPath = fileChooserQueries.getSelectedFile().getAbsolutePath();
                                    SwingUtilities.invokeLater(() -> onQueriesPath.publish(selectedPath));
                                }
                            });
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonQueriesSave = new JButton("Save") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                            addActionListener(evt -> onQueriesPath.publish(fileChooserQueries.getSelectedFile().getAbsolutePath()));
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonQueriesSaveAs = new JButton("Save as ...") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                            addActionListener(evt -> {
                                if (fileChooserQueries.showDialog(root, "Save Queries") == JFileChooser.APPROVE_OPTION) {
                                    final String selectedPath = fileChooserQueries.getSelectedFile().getAbsolutePath();
                                    SwingUtilities.invokeLater(() -> onQueriesSave.publish(selectedPath));
                                }
                            });
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
                        add(buttonReportGenerate = new JButton("Generate") {{
                            setBorder(Styles.BORDER_EMPTY_FIELD_SMALL);
                            setEnabled(false);
                            setFocusPainted(false);
                            setFont(Styles.FONT_SMALL);
                        }});
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
    public EventPublisher<String> onSystemPath() {
        return onSystemPath;
    }

    @Override
    public EventPublisher<String> onQueriesPath() {
        return onQueriesPath;
    }

    @Override
    public EventPublisher<String> onQueriesSave() {
        return onQueriesSave;
    }

    @Override
    public void setSystemPath(final String pathString) {
        final boolean isSet = pathString != null;
        if (isSet) {
            final File file = new File(pathString);
            fileChooserSystem.setSelectedFile(file);
            fileChooserQueries.setSelectedFile(file.getParentFile());
        }
        buttonQueriesLoad.setEnabled(isSet);
        buttonQueriesSaveAs.setEnabled(isSet);
        textAreaQueries.setEnabled(isSet);
        buttonReportGenerate.setEnabled(isSet);
    }

    @Override
    public void setSystemStatus(final Status status, final String systemName) {
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

    @Override
    public void setQueriesPath(final String pathString) {
        final boolean isSet = pathString != null;
        if (isSet) {
            fileChooserQueries.setSelectedFile(new File(pathString));
        }
        buttonQueriesSave.setEnabled(isSet);
    }

    @Override
    public void setQueriesStatus(final Status status, final String queriesName) {
        switch (status) {
            case NOT_FOUND:
                labelQueriesStatus.setForeground(Styles.COLOR_ERROR);
                labelQueriesStatus.setText("Path does not identity an existing file.");
                break;
            case NOT_PROVIDED:
                labelQueriesStatus.setForeground(Styles.COLOR_ERROR);
                labelQueriesStatus.setText("Please provide a valid UPPAAL queries file (*.q) path.");
                break;
            case NOT_VALID:
                labelQueriesStatus.setForeground(Styles.COLOR_ERROR);
                labelQueriesStatus.setText("Path does not identity a valid UPPAAL queries file.");
                break;
            case OK:
                labelQueriesStatus.setForeground(Styles.COLOR_FOREGROUND_PRIMARY);
                labelQueriesStatus.setText(queriesName + " ");
                break;
        }
    }

    @Override
    public void setReport(final String report) {
        textAreaReport.setEnabled(true);
        textAreaReport.setText(report);
    }
}
