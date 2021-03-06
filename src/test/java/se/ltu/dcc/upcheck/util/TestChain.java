package se.ltu.dcc.upcheck.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestChain {
    @Test
    public void shouldIterateThroughAllElements() {
        final Chain<Integer> chain0 = Chain.of(3, 2, 1);
        {
            final List<Integer> list0 = new ArrayList<>();
            chain0.forEach(list0::add);

            assertEquals(Arrays.asList(3, 2, 1), list0);
            assertEquals(3, list0.size());
        }
        final Chain<Integer> chain1 = chain0.prepend(4);
        {
            final List<Integer> list1 = new ArrayList<>();
            chain1.forEach(list1::add);

            assertEquals(Arrays.asList(4, 3, 2, 1), list1);
            assertEquals(4, list1.size());
        }
    }
}
