package se.ltu.d7031e.emapal4.upcheck.view;

/**
 * Various {@link Renderer} utilities.
 */
public class Renderers {
    /**
     * @return New window-based {@link Renderer}.
     */
    public static Renderer<? extends View> CreateWindowRenderer() {
        return new Window();
    }
}
