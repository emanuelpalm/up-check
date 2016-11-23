package se.ltu.d7031e.emapal4.upcheck.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Wraps some {@link ClassLoader}, providing a facade of reflection helpers.
 */
public class DynamicFactory {
    private final ClassLoader classLoader;

    /**
     * @param classLoader class loader to wrap
     */
    public DynamicFactory(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Gets named public constructor associated with named class, having any identified arguments.
     *
     * @param className name of class to get constructor of
     * @param arguments constructor arguments
     * @return constructor object
     */
    public Constructor<?> constructor(final String className, final Class<?>... arguments) {
        try {
            return classLoader.loadClass(className).getConstructor(arguments);

        } catch (final Exception e) {
            throw new DynamicException(e);
        }
    }

    /**
     * Creates new instance of class identified by class name.
     *
     * @param className class to create instance of
     * @param arguments constructor arguments
     * @return class instance
     */
    public DynamicObject create(final String className, final Object... arguments) {
        return new DynamicObject(createRaw(className, arguments));
    }

    private Object createRaw(final String className, final Object[] arguments) {
        try {
            final Class<?> clazz = classLoader.loadClass(className);
            if (arguments.length == 0) {
                return clazz.newInstance();
            }
            return clazz.getConstructor(Arrays
                    .stream(arguments)
                    .map(Object::getClass)
                    .collect(Collectors.toList())
                    .toArray(new Class<?>[arguments.length]))
                    .newInstance(arguments);

        } catch (final Exception e) {
            throw new DynamicException(e);
        }
    }
}
