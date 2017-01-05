package se.ltu.dcc.upcheck.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents some lazily evaluated value.
 *
 * @param <T> value type
 */
public class Lazy<T> {
    private Supplier<T> supplier;
    private T value;

    /**
     * Creates new lazy value, using provided supplier to instantiate value when first requested,
     *
     * @param supplier function used to instantiate value
     */
    public Lazy(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * @return lazy value
     */
    public T value() {
        synchronized (this) {
            if (supplier != null) {
                value = supplier.get();
                supplier = null;
            }
            return value;
        }
    }

    /**
     * Executes given function with lazy value, only if value has been created be a prior call to {@link #value()}.
     *
     * @param consumer function to execute
     */
    public void ifValueCreated(final Consumer<T> consumer) {
        synchronized (this) {
            if (supplier == null) {
                consumer.accept(value);
            }
        }
    }

    /**
     * @return whether or not lazy value has been instantiated
     */
    public boolean isValueCreated() {
        synchronized (this) {
            return supplier == null;
        }
    }
}
