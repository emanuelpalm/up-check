package se.ltu.d7031e.emapal4.upcheck.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntBinaryOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDynamicInterface {
    @Test
    public void shouldInvokeProxyMethodWithoutParameters() {
        final DynamicFactory dynamicFactory = new DynamicFactory();
        final DynamicInterface dynamicInterface = dynamicFactory.newInterfaceInstance(Runnable.class.getName());

        final AtomicBoolean atomicRunIsCalled = new AtomicBoolean(false);
        dynamicInterface.registerProxyMethod("run", new Class<?>[0], args -> {
            atomicRunIsCalled.set(true);
            return null;
        });
        ((Runnable) dynamicInterface.unwrap()).run();

        assertTrue(atomicRunIsCalled.get());
    }

    @Test
    public void shouldInvokeProxyMethodWithParameters() {
        final DynamicFactory dynamicFactory = new DynamicFactory();
        final DynamicInterface dynamicInterface = dynamicFactory.newInterfaceInstance(IntBinaryOperator.class.getName());

        dynamicInterface.registerProxyMethod("applyAsInt", new Class<?>[]{int.class, int.class}, args ->
                (int) args[0] + (int) args[1]);
        final int result = ((IntBinaryOperator) dynamicInterface.unwrap()).applyAsInt(1200, 34);

        assertEquals(1234, result);
    }

    @Test(expected = DynamicException.class)
    public void shouldThrowExceptionIfInvokingUnregisteredMethod() {
        final DynamicFactory dynamicFactory = new DynamicFactory();
        final DynamicInterface dynamicInterface = dynamicFactory.newInterfaceInstance(IntBinaryOperator.class.getName());

        ((IntBinaryOperator) dynamicInterface.unwrap()).applyAsInt(1200, 34);
    }

}
