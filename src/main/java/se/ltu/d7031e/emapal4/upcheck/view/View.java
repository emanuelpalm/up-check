package se.ltu.d7031e.emapal4.upcheck.view;

/**
 * An object that can act as an MVC view.
 */
public interface View {
    /**
     * @param message Error message to be displayed to application user.
     * @param e       Exception to be displayed to application user.
     */
    void showException(final String message, final Throwable e);
}
