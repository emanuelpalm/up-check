package se.ltu.d7031e.emapal4.upcheck.util;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A dynamically instantiated interface.
 *
 * Allows object method invocations to be captured via {@link #registerProxyMethod(String, Class[], Function)}. If some
 * non-captured method is invoked, a {@link DynamicException} is thrown.
 */
public class DynamicInterface {
    private final Object object;
    private final TreeMap<String, Function<Object[], Object>> proxyMethods = new TreeMap<>();

    /**
     * Creates new dynamic interface instance using provided class loader and interface class name.
     *
     * @param classLoader class loader to use for instantiating interface
     * @param className   name of instantiated interface
     */
    DynamicInterface(final ClassLoader classLoader, final String className) {
        try {
            object = Proxy.newProxyInstance(classLoader, new Class<?>[]{classLoader.loadClass(className)}, (proxy, method, args) -> {
                final String methodName = method.getName() + Arrays
                        .stream(method.getParameterTypes())
                        .map(Object::toString)
                        .collect(Collectors.joining(",", "(", ")"));
                final Function<Object[], Object> proxyMethod = proxyMethods.getOrDefault(methodName, args0 -> {
                    throw new DynamicException(new IllegalStateException("Unimplemented interface method invoked."));
                });
                return proxyMethod.apply(args);
            });
        } catch (final ClassNotFoundException e) {
            throw new DynamicException(e);
        }
    }

    /**
     * @return wrapped interface instance
     */
    public Object unwrap() {
        return object;
    }

    /**
     * Registers method handler, replacing any previously set with the same name and arguments.
     * <p>
     * The method handler is invoked whenever the interface method with the provided name and matching method argument
     * classes is called.
     *
     * @param name       name of interface method
     * @param parameters array of method parameter classes
     * @param handler    function used to handle method invocations
     */
    public void registerProxyMethod(final String name, final Class<?>[] parameters, final Function<Object[], Object> handler) {
        proxyMethods.put(DynamicMethod.resolveName(name, parameters), handler);
    }
}
