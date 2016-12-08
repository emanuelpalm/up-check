package se.ltu.d7031e.emapal4.upcheck.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLazy {
    @Test
    public void shouldInstantiateValueLazily() {
        final AtomicBoolean isValueCreated = new AtomicBoolean(false);
        final Lazy<Integer> lazyInteger = new Lazy<>(() -> {
            isValueCreated.set(true);
            return 1234;
        });

        assertFalse(isValueCreated.get());
        assertFalse(lazyInteger.isValueCreated());

        assertEquals(1234, (int) lazyInteger.value());

        assertTrue(isValueCreated.get());
        assertTrue(lazyInteger.isValueCreated());
    }

    @Test
    public void shouldOnlyExecuteFunctionIfValueIsCreated() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final Lazy<Integer> lazyInteger = new Lazy<>(() -> 100);

        lazyInteger.ifValueCreated(atomicInteger::addAndGet);

        assertEquals(100, (int) lazyInteger.value());
        assertEquals(0, atomicInteger.get());

        lazyInteger.ifValueCreated(atomicInteger::addAndGet);

        assertEquals(100, atomicInteger.get());
    }
}
