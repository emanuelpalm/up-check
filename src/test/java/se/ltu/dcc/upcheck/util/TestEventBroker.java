package se.ltu.dcc.upcheck.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TestEventBroker {
    @Test
    public void shouldSendEventsToSubscribers() {
        final AtomicInteger counter0 = new AtomicInteger(0);
        final AtomicInteger counter1 = new AtomicInteger(1);
        {
            final EventBroker<Integer> publisher = new EventBroker<>();

            publisher.subscribe(counter0::getAndAdd);
            publisher.subscribe(counter1::getAndAdd);

            publisher.publish(4);
            publisher.publish(30);
            publisher.publish(200);
            publisher.publish(1000);
        }
        assertEquals(1234, counter0.get());
        assertEquals(1235, counter1.get());
    }

    @Test
    public void shouldNotSendEventsToUnsubscribedReceivers() {
        final AtomicInteger counter0 = new AtomicInteger(0);
        final AtomicInteger counter1 = new AtomicInteger(1);
        {
            final EventBroker<Integer> publisher = new EventBroker<>();

            final EventSubscription s0 = publisher.subscribe(counter0::getAndAdd);
            final EventSubscription s1 = publisher.subscribe(counter1::getAndAdd);

            s0.cancel();
            publisher.publish(1234);
            s1.cancel();
        }
        assertEquals(0, counter0.get());
        assertEquals(1235, counter1.get());
    }
}
