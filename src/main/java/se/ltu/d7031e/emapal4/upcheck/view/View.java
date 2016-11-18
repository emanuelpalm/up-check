package se.ltu.d7031e.emapal4.upcheck.view;

/**
 * An object that can act as an MVC view.
 */
public interface View {
    /**
     * @param e Irrecoverable exception to be displayed to application user.
     */
    void showException(final Throwable e);
}
