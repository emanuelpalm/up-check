package se.ltu.dcc.upcheck.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable singly linked list.
 *
 * @param <T> link element type
 */
public abstract class Chain<T> implements Iterable<T> {
    private Chain() {}

    /**
     * Creates new empty chain.
     *
     * @param <T> chain link element type
     * @return empty chain
     */
    @SuppressWarnings("unchecked")
    public static <T> Chain<T> empty() {
        return (Chain<T>) End.INSTANCE;
    }

    /**
     * Creates new chain from given elements.
     *
     * @param elements chain elements
     * @param <T>      element type
     * @return created chain
     */
    @SafeVarargs
    public static <T> Chain<T> of(final T... elements) {
        Chain<T> chain = empty();
        for (int i = elements.length; i-- != 0; ) {
            chain = chain.prepend(elements[i]);
        }
        return chain;
    }

    /**
     * @return head of chain, unless chain is empty
     */
    public abstract Optional<T> head();

    /**
     * @return all links of chain following this
     */
    public abstract Chain<T> tail();

    /**
     * @return amount of elements in chain
     */
    public abstract int size();

    /**
     * Adds element to beginning of chain.
     *
     * @param element prepended element
     * @return chain with element prepended
     * @throws NullPointerException if element is {@code null}
     */
    public Chain<T> prepend(final T element) {
        return new Link<>(element, this);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Chain<T> chain = Chain.this;

            @Override
            public boolean hasNext() {
                return chain instanceof Link<?>;
            }

            @Override
            public T next() {
                try {
                    return chain.head().orElse(null);

                } finally {
                    chain = chain.tail();
                }
            }
        };
    }

    private static final class Link<T> extends Chain<T> {
        private final T element;
        private final Chain<T> next;

        Link(final T element, final Chain<T> next) {
            this.element = Objects.requireNonNull(element);
            this.next = Objects.requireNonNull(next);
        }

        @Override
        public Optional<T> head() {
            return Optional.ofNullable(element);
        }

        @Override
        public Chain<T> tail() {
            return next;
        }

        @Override
        public int size() {
            return 1 + next.size();
        }
    }

    private static final class End<T> extends Chain<T> {
        private static final End<?> INSTANCE = new End<>();

        @Override
        public Optional<T> head() {
            return Optional.empty();
        }

        @Override
        public Chain<T> tail() {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }
    }
}
