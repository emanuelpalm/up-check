package se.ltu.d7031e.emapal4.upcheck.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TestDynamicObject {
    @Test
    public void shouldCallMethods() {
        final DynamicObject wrappedInteger = new DynamicObject(new AtomicInteger(0));

        assertEquals("0", wrappedInteger.unwrap().toString());

        assertEquals(5, wrappedInteger.method("addAndGet", int.class).invoke(5));
        assertEquals(6, wrappedInteger.invoke("incrementAndGet"));
    }
}
