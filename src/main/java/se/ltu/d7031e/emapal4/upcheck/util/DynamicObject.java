package se.ltu.d7031e.emapal4.upcheck.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Wraps an object, providing a facade of reflection helpers.
 */
public class DynamicObject {
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mapClassToMethods = new ConcurrentHashMap<>(8);

    private final Object object;
    private final Map<String, Method> objectMethods;

    /**
     * @param object object to wrap
     */
    public DynamicObject(final Object object) {
        this.object = object;
        objectMethods = mapClassToMethods.computeIfAbsent(object.getClass(), objectClass -> new ConcurrentHashMap<>(4));
    }

    /**
     * @return wrapped object
     */
    public Object unwrap() {
        return object;
    }

    /**
     * Invokes named public non-static method with provided arguments.
     * <p>
     * Note that this method cannot be used to invoke methods with primitive or generic type arguments.
     *
     * @param methodName name of invoked method
     * @param arguments  arbitrary arguments
     * @return value returned by invoked method
     */
    public Object invoke(final String methodName, final Object... arguments) {
        final DynamicMethod method = method(methodName, Arrays.stream(arguments)
                .map(Object::getClass)
                .collect(Collectors.toList())
                .toArray(new Class<?>[arguments.length]));

        return method.invoke(arguments);
    }

    /**
     * Gets named public non-static method with provided arguments.
     *
     * @param name      name of requested method
     * @param arguments arbitrary arguments
     * @return value returned by invoked method
     */
    public DynamicMethod method(final String name, final Class<?>... arguments) {
        final Method method = objectMethods.computeIfAbsent(resolveMethodName(name, arguments), ignored -> {
            try {
                return object.getClass().getMethod(name, arguments);

            } catch (final Exception e) {
                throw new DynamicException(e);
            }
        });
        return new DynamicMethod(object, method);
    }

    private static String resolveMethodName(final String simpleName, final Class<?>... arguments) {
        return Arrays.stream(arguments)
                .map(Class::getName)
                .collect(Collectors.joining(",", simpleName + "(", ")"));
    }
}
