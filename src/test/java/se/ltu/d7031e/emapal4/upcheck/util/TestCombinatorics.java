package se.ltu.d7031e.emapal4.upcheck.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCombinatorics {
    @Test
    public void shouldProduceCorrect2to1Combinations() {
        final ArrayList<List<Integer>> result = new ArrayList<>();
        Combinatorics.combinations(Arrays.asList(10, 20), result::add);

        assertEquals(new ArrayList<List<Integer>>() {{
            add(Collections.singletonList(10));
            add(Collections.singletonList(20));
            add(Arrays.asList(10, 20));
        }}, result);
    }

    @Test
    public void shouldProduceCorrect3to1Combinations() {
        final ArrayList<List<Integer>> result = new ArrayList<>();
        Combinatorics.oneToKCombinations(Arrays.asList(10, 20, 30, 40), 3, result::add);

        assertEquals(new ArrayList<List<Integer>>() {{
            add(Collections.singletonList(10));
            add(Collections.singletonList(20));
            add(Collections.singletonList(30));
            add(Collections.singletonList(40));
            add(Arrays.asList(10, 20));
            add(Arrays.asList(10, 30));
            add(Arrays.asList(10, 40));
            add(Arrays.asList(20, 30));
            add(Arrays.asList(20, 40));
            add(Arrays.asList(30, 40));
            add(Arrays.asList(10, 20, 30));
            add(Arrays.asList(10, 20, 40));
            add(Arrays.asList(10, 30, 40));
            add(Arrays.asList(20, 30, 40));
        }}, result);
    }

    @Test
    public void shouldProduceCorrect2Combinations() {
        final ArrayList<List<Integer>> result = new ArrayList<>();
        Combinatorics.kCombinations(Arrays.asList(10, 20, 30, 40), 2, result::add);

        assertEquals(new ArrayList<List<Integer>>() {{
            add(Arrays.asList(10, 20));
            add(Arrays.asList(10, 30));
            add(Arrays.asList(10, 40));
            add(Arrays.asList(20, 30));
            add(Arrays.asList(20, 40));
            add(Arrays.asList(30, 40));
        }}, result);
    }

    @Test
    public void shouldProduceCorrect3Combinations() {
        final ArrayList<List<Integer>> result = new ArrayList<>();
        Combinatorics.kCombinations(Arrays.asList(10, 20, 30, 40), 3, result::add);

        assertEquals(new ArrayList<List<Integer>>() {{
            add(Arrays.asList(10, 20, 30));
            add(Arrays.asList(10, 20, 40));
            add(Arrays.asList(10, 30, 40));
            add(Arrays.asList(20, 30, 40));
        }}, result);
    }
}
