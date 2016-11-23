package se.ltu.d7031e.emapal4.upcheck.view;

import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Various styles.
 */
class Styles {
    static final EmptyBorder BORDER_EMPTY_FIELD;
    static final EmptyBorder BORDER_EMPTY_FIELD_SMALL;
    static final EmptyBorder BORDER_EMPTY_MEDIUM;
    static final EmptyBorder BORDER_EMPTY_MEDIUM_NO_TOP;
    static final EmptyBorder BORDER_EMPTY_SMALL;

    static final Color COLOR_BACKGROUND_PRIMARY = new Color(240, 240, 240);
    static final Color COLOR_BACKGROUND_SECONDARY = new Color(95, 95, 95);
    static final Color COLOR_ERROR = new Color(190, 67, 66);
    static final Color COLOR_FOREGROUND_PRIMARY = new Color(28, 28, 28);
    static final Color COLOR_FOREGROUND_SECONDARY = new Color(255, 255, 255);

    static final Font FONT_ERROR = new Font("Sans", Font.BOLD, 16);
    static final Font FONT_HEADING1 = new Font("Sans", Font.PLAIN, 32);
    static final Font FONT_PARAGRAPH = new Font("Sans", Font.PLAIN, 16);
    static final Font FONT_SMALL = new Font("Sans", Font.PLAIN, 11);

    static final int SPACING_SMALLER = 6;
    static final int SPACING_SMALL = 10;
    static final int SPACING_MEDIUM = 16;

    static {
        BORDER_EMPTY_FIELD = new EmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM);
        BORDER_EMPTY_FIELD_SMALL = new EmptyBorder(SPACING_SMALLER, SPACING_SMALL, SPACING_SMALLER, SPACING_SMALL);
        BORDER_EMPTY_MEDIUM = new EmptyBorder(SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM);
        BORDER_EMPTY_MEDIUM_NO_TOP = new EmptyBorder(0, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM);
        BORDER_EMPTY_SMALL = new EmptyBorder(SPACING_SMALL, SPACING_SMALL, SPACING_SMALL, SPACING_SMALL);
    }
}
