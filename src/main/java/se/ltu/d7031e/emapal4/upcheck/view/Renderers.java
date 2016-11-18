package se.ltu.d7031e.emapal4.upcheck.view;

import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Various {@link Renderer} and {@link View} utilities.
 */
public class Renderers {
    private static final Map<Class<?>, Class<? extends WindowView>> mapViewToWindowView;

    /**
     * @return New window-based {@link Renderer}.
     */
    public static Renderer<? extends View> CreateWindowRenderer() {
        return new Window();
    }

    /**
     * Creates concrete instance of provided {@link View} interface class.
     *
     * @param viewClass Class of interface extending {@link View}, having a concrete implementation in this package.
     * @return Created instance.
     * @throws ClassNotFoundException No concrete implementation of identified {@link View} available in package.
     * @throws IllegalAccessException Attempt to access protected class.
     * @throws InstantiationException Could not instantiate concrete {@link View} implementation.
     */
    public static View CreateView(final Class<? extends View> viewClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<? extends WindowView> windowViewClass = mapViewToWindowView.get(viewClass);
        if (windowViewClass == null) {
            throw new ClassNotFoundException("No concrete " + viewClass + " implementation available in package.");
        }
        return windowViewClass.newInstance();
    }

    static {
        final Reflections reflections = new Reflections(Renderers.class.getPackage().getName());
        final Set<Class<? extends WindowView>> windowViewClasses = reflections.getSubTypesOf(WindowView.class);

        mapViewToWindowView = new HashMap<>(windowViewClasses.size());
        for (final Class<? extends WindowView> windowViewClass : windowViewClasses) {
            Class<?> viewClass = null;
            for (final Class<?> i : windowViewClass.getInterfaces()) {
                if (View.class.isAssignableFrom(i) && Modifier.isPublic(i.getModifiers())) {
                    viewClass = i;
                    break;
                }
            }
            if (viewClass == null || !viewClass.isInterface()) {
                throw new IllegalStateException(windowViewClass + " not implementing interface extending " + View.class.getName() + ".");
            }
            mapViewToWindowView.put(viewClass, windowViewClass);
        }
    }
}
