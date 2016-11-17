package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.*;
import java.awt.*;

/**
 * {@link View} useful for locating a local UPPAAL installation.
 */
public class ViewLocateUppaal implements WindowView {
    @Override
    public JPanel panel() throws Exception {
        return new JPanel(new BorderLayout()) {{
            setBackground(Styles.COLOR_BACKGROUND_PRIMARY);

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
                add(new JPanel() {{
                    setBorder(Styles.BORDER_EMPTY_MEDIUM);
                    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                    add(new JLabel("Path") {{
                        setFont(Styles.FONT_PARAGRAPH);
                    }});
                    add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                    add(Box.createHorizontalGlue());
                    add(new JTextField() {{
                    }});
                    add(Box.createRigidArea(new Dimension(Styles.SPACING_MEDIUM, 0)));
                    add(new JButton("Select ...") {{
                        setFocusPainted(false);
                        setFont(Styles.FONT_PARAGRAPH);
                    }});
                }});
            }}, BorderLayout.PAGE_START);

            add(new JPanel() {{
                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                setBorder(Styles.BORDER_EMPTY_MEDIUM);
                add(Box.createHorizontalGlue());
                add(new JButton("Confirm") {{
                    setBorder(Styles.BORDER_EMPTY_FIELD);
                    setFocusPainted(false);
                    setFont(Styles.FONT_PARAGRAPH);
                }});
            }}, BorderLayout.PAGE_END);

        }};
    }
}
