package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.view.Renderer;
import se.ltu.d7031e.emapal4.upcheck.view.View;
import se.ltu.d7031e.emapal4.upcheck.view.Views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Allows {@link Controller}s to navigate between different {@link View}/{@link Controller} pairs.
 */
public class Navigator {
    private final Renderer<? extends View> renderer;
    private final Method rendererMethodSetView;

    /**
     * @param renderer Renderer used to render current {@link View}.
     */
    public Navigator(final Renderer<? extends View> renderer) {
        this.renderer = renderer;
        try {
            rendererMethodSetView = renderer
                    .getClass()
                    .getMethod("setView", renderer.viewClass());

            rendererMethodSetView.setAccessible(true);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Navigates to given {@link Controller}, which replaces the one currently displayed by {@link Renderer}.
     *
     * The {@link View} associated with given {@link Controller} may be instantiated via reflection when this method is
     * called. If such an operation would fail, an appropriate exception is thrown.
     *
     * @param controller Controller to navigate to.
     * @throws ReflectiveOperationException A reflection operation failed.
     */
    public void navigateTo(final Controller<? extends View> controller) throws ReflectiveOperationException {
        final Class<? extends View> viewClass = controller.viewClass();
        final Method controllerMethodRegister = controller
                .getClass()
                .getMethod("register", Navigator.class, viewClass);

        final Object view = Views.newInstance(viewClass);
        controllerMethodRegister.invoke(controller, this, view);
        rendererMethodSetView.invoke(renderer, view);
    }
}
