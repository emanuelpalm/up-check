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
    private final EventBroker<ViewVerifySystem.Queries> onQueriesSave = new EventBroker<>();
    private final EventBroker<Void> onReportClear = new EventBroker<>();
    private final EventBroker<ViewVerifySystem.Queries> onReportRequest = new EventBroker<>();

    private JLabel labelSystemStatus;

    private JButton buttonQueriesLoad;
    private JButton buttonQueriesReload;
    private JButton buttonQueriesSave;
    private JButton buttonQueriesSaveAs;
    private JLabel labelQueriesStatus;
    private JTextArea textAreaQueries;

    private JButton buttonReportClear;
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
                        setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                    }});
                    add(Box.createHorizontalGlue());
                    add(new JButton("Load ...") {{
                        setFocusPainted(false);
                        addActionListener(evt -> {
                            if (fileChooserSystem.showDialog(root, "Load System") == JFileChooser.APPROVE_OPTION) {
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
                    add(labelSystemStatus = new JLabel(" "));
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
                            setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                        }});
                        add(Box.createHorizontalGlue());
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));

                        add(buttonQueriesReload = new JButton("Reload") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> {
                                final int option = JOptionPane.showConfirmDialog(
                                        root,
                                        "Note that this will override any unsaved changes.",
                                        "Confirm Reload",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.WARNING_MESSAGE);
                                if (option == JOptionPane.OK_OPTION) {
                                    onQueriesPath.publish(fileChooserQueries.getSelectedFile().getAbsolutePath());
                                }
                            });
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonQueriesLoad = new JButton("Load ...") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> {
                                if (fileChooserQueries.showDialog(root, "Load Queries") == JFileChooser.APPROVE_OPTION) {
                                    final String selectedPath = fileChooserQueries.getSelectedFile().getAbsolutePath();
                                    SwingUtilities.invokeLater(() -> onQueriesPath.publish(selectedPath));
                                }
                            });
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonQueriesSave = new JButton("Save") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> onQueriesSave.publish(new ViewVerifySystem.Queries(
                                    fileChooserQueries.getSelectedFile().getAbsolutePath(),
                                    textAreaQueries.getText())));
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonQueriesSaveAs = new JButton("Save as ...") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> {
                                if (fileChooserQueries.showDialog(root, "Save Queries") == JFileChooser.APPROVE_OPTION) {
                                    final String selectedPath = fileChooserQueries.getSelectedFile().getAbsolutePath();
                                    SwingUtilities.invokeLater(() -> onQueriesSave.publish(new ViewVerifySystem.Queries(
                                            selectedPath,
                                            textAreaQueries.getText())));
                                }
                            });
                        }});
                    }});
                    add(new JPanel() {{
                        setBorder(Styles.BORDER_EMPTY_SMALL);
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        setOpaque(false);
                        add(labelQueriesStatus = new JLabel(" "));
                        add(Box.createHorizontalGlue());
                    }});
                    add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        add(new JScrollPane(textAreaQueries = new JTextArea() {{
                            setBorder(Styles.BORDER_EMPTY_SMALL);
                            setColumns(78);
                            setEnabled(false);
                            setFont(Styles.FONT_MONOSPACED);
                            setRows(8);
                        }}, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {{
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
                            setForeground(Styles.COLOR_FOREGROUND_SECONDARY);
                        }});
                        add(Box.createHorizontalGlue());
                        add(buttonReportGenerate = new JButton("Generate") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> onReportRequest.publish(new ViewVerifySystem.Queries(
                                    null,
                                    textAreaQueries.getText())));
                        }});
                        add(Box.createHorizontalStrut(Styles.SPACING_SMALL));
                        add(buttonReportClear = new JButton("Clear") {{
                            setEnabled(false);
                            setFocusPainted(false);
                            addActionListener(evt -> {
                                final int option = JOptionPane.showConfirmDialog(
                                        root,
                                        "Note that this will remove the current system report.",
                                        "Confirm report clearing",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.WARNING_MESSAGE);
                                if (option == JOptionPane.OK_OPTION) {
                                    textAreaReport.setText("");
                                    onReportClear.publish(null);
                                }
                            });
                        }});
                    }});
                    add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                        add(new JScrollPane(textAreaReport = new JTextArea() {{
                            setBorder(Styles.BORDER_EMPTY_SMALL);
                            setColumns(78);
                            setEditable(false);
                            setEnabled(false);
                            setFont(Styles.FONT_MONOSPACED);
                            setRows(8);
                        }}, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {{
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
    public EventPublisher<ViewVerifySystem.Queries> onQueriesSave() {
        return onQueriesSave;
    }

    @Override
    public EventPublisher<Void> onReportCleared() {
        return onReportClear;
    }

    @Override
    public EventPublisher<ViewVerifySystem.Queries> onReportRequest() {
        return onReportRequest;
    }

    @Override
    public void setSystemPath(final String pathString) {
        if (pathString != null) {
            final File file = new File(pathString);
            fileChooserSystem.setSelectedFile(file);
            fileChooserQueries.setCurrentDirectory(file.getParentFile());
        }
    }

    @Override
    public void setQueries(final String queries) {
        textAreaQueries.setText(queries);
    }

    @Override
    public void setSystemStatus(final Status status, final String systemName) {
        if (status == Status.OK) {
            setSystemStatusOK(systemName);
        } else {
            setSystemStatusError(status, systemName);
        }
    }

    private void setSystemStatusError(final Status status, final String systemName) {
        switch (status) {
            case NOT_FOUND:
                labelSystemStatus.setText("Selected path does not identity an existing file.");
                break;
            case NOT_LOADED:
                labelSystemStatus.setText("'" + systemName + "' could not be loaded. Are proper permissions set and the file available?");
                break;
            case NOT_PROVIDED:
                labelSystemStatus.setText("Please provide a valid UPPAAL system path.");
                break;
            case NOT_VALID:
                labelSystemStatus.setText("'" + systemName + "' contains errors and could not be loaded.");
                break;
            default:
                throw new IllegalStateException("Unhandled status: " + status);
        }
        labelSystemStatus.setForeground(Styles.COLOR_ERROR);
        buttonQueriesLoad.setEnabled(false);
        buttonQueriesSaveAs.setEnabled(false);
        textAreaQueries.setEnabled(false);
        buttonReportGenerate.setEnabled(false);
        buttonReportClear.setEnabled(false);
    }

    private void setSystemStatusOK(final String systemName) {
        labelSystemStatus.setForeground(Styles.COLOR_FOREGROUND_PRIMARY);
        labelSystemStatus.setText(systemName != null ? systemName + " " : " ");
        buttonQueriesLoad.setEnabled(true);
        buttonQueriesSaveAs.setEnabled(true);
        textAreaQueries.setEnabled(true);
        buttonReportGenerate.setEnabled(true);
        buttonReportClear.setEnabled(true);
    }

    @Override
    public void setQueriesPath(final String pathString) {
        final boolean isSet = pathString != null;
        if (isSet) {
            fileChooserQueries.setSelectedFile(new File(pathString));
        }
        buttonQueriesSave.setEnabled(isSet);
        buttonQueriesReload.setEnabled(isSet);
    }

    @Override
    public void setQueriesStatus(final Status status, final String queriesName) {
        if (status == Status.OK) {
            setQueriesStatusOK(queriesName);
        } else {
            setQueriesStatusError(status, queriesName);
        }
    }

    private void setQueriesStatusError(final Status status, final String queriesName) {
        switch (status) {
            case NOT_FOUND:
                labelQueriesStatus.setText("Path does not identity an existing file.");
                break;
            case NOT_LOADED:
                labelSystemStatus.setText("'" + queriesName + "' could not be loaded. Are proper permissions set and the file available?");
                break;
            case NOT_PROVIDED:
                labelQueriesStatus.setText("Please provide a valid UPPAAL queries file (*.q) path.");
                break;
            case NOT_VALID:
                labelQueriesStatus.setText("Path does not identity a valid UPPAAL queries file.");
                break;
            default:
                throw new IllegalStateException("Unhandled status: " + status);
        }
        labelQueriesStatus.setForeground(Styles.COLOR_ERROR);
    }

    private void setQueriesStatusOK(final String queriesName) {
        labelQueriesStatus.setForeground(Styles.COLOR_FOREGROUND_PRIMARY);
        labelQueriesStatus.setText(queriesName != null ? queriesName + " " : " ");
    }

    @Override
    public void addReport(final String report) {
        textAreaReport.setEnabled(true);
        textAreaReport.append(report + "\r\n\r\n");
    }
}
