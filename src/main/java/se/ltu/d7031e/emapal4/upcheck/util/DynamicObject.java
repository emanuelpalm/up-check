package se.ltu.d7031e.emapal4.upcheck.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Wraps an object, providing a facade of reflection helpers.
 */
public class DynamicObject {
    private final Object object;
    private final TreeMap<String, Method> objectMethods = new TreeMap<>();

    /**
     * @param object object to wrap
     */
    public DynamicObject(final Object object) {
        this.object = object;
    }

    /**
     * @return wrapped object
     */
    public Object unwrap() {
        return object;
    }

    /**
     * Invokes named public non-static method with provided arguments.
     *
     * @param methodName name of invoked method
     * @param arguments  arbitrary arguments
     * @return value returned by invoked method
     */
    public Object invoke(final String methodName, final Object... arguments) {
        try {
            return objectMethods.compute(methodName, (name, existingMethod) -> {
                try {
                    final Method method;
                    if (existingMethod != null) {
                        method = existingMethod;
                    } else {
                        final Class<?>[] argumentClasses = Arrays
                                .stream(arguments)
                                .map(Object::getClass)
                                .collect(Collectors.toList())
                                .toArray(new Class<?>[arguments.length]);

                        method = object.getClass().getMethod(methodName, argumentClasses);
                    }
                    return method;
                } catch (final Exception e) {
                    throw new DynamicObjectException(e);
                }
            }).invoke(object, arguments);

        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new DynamicObjectException(e);
        }
    }
}
