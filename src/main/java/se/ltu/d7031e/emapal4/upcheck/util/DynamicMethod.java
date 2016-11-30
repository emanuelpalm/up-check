package se.ltu.d7031e.emapal4.upcheck.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A dynamically loaded class method, maintaining a reference to a class object.
 */
public class DynamicMethod {
    private final Object object;
    private final Method method;

    /**
     * Creates new DynamicMethod of given object and method.
     *
     * @param object target object
     * @param method object method
     */
    public DynamicMethod(final Object object, final Method method) {
        this.object = object;
        this.method = method;
    }

    /**
     * Invokes dynamic method with provided arguments.
     *
     * @param arguments method arguments
     * @return method return value, if any
     */
    public Object invoke(final Object... arguments) {
        try {
            return method.invoke(object, arguments);

        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new DynamicException(e);
        }
    }

    /**
     * Resolves method identifier from given name and parameters.
     *
     * @param simpleName name of resolved method
     * @param parameters arbitrary parameters
     * @return string representation of method
     */
    static String resolveName(final String simpleName, final Class<?>... parameters) {
        return Arrays.stream(parameters)
                .map(Class::getName)
                .collect(Collectors.joining(",", simpleName + "(", ")"));
    }
}
