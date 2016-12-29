package se.ltu.d7031e.emapal4.upcheck.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Various combination utilities.
 */
public class Combinatorics {
    /**
     * Produces all combinations of {@code source} elements, with each combination never containing the same source
     * element more than once. Combinations are presented via given {@code combiner}.
     *
     * @param source   origin of combined elements
     * @param combiner closure provided with any produced combinations
     * @param <E>      element type
     */
    public static <E> void combinations(final List<E> source, final Consumer<List<E>> combiner) {
        oneToKCombinations(source, source.size(), combiner);
    }

    /**
     * Produces all combinations of {@code source} elements of {@code k..1} members, with each combination never
     * containing the same source element more than once. Combinations are presented via given {@code combiner}.
     *
     * @param source   origin of combined elements
     * @param k        size, in elements, of produced combinations
     * @param combiner closure provided with any produced combinations
     * @param <E>      element type
     */
    public static <E> void oneToKCombinations(final List<E> source, final int k, final Consumer<List<E>> combiner) {
        for (int k0 = 1; k0 <= k; ++k0) {
            kCombinations(source, k0, combiner);
        }
    }

    /**
     * Produces all combinations of {@code source} elements of {@code k} members, with each combination never
     * containing the same source element more than once. Combinations are presented via given {@code combiner}.
     *
     * @param source   origin of combined elements
     * @param k        size, in elements, of produced combinations
     * @param combiner closure provided with any produced combinations
     * @param <E>      element type
     */
    public static <E> void kCombinations(final List<E> source, final int k, final Consumer<List<E>> combiner) {
        final int sourceSize = source.size();
        if (k > sourceSize) {
            return;
        }
        final int[] indexes = new int[k];
        for (int index = 0; index < k; ++index) {
            indexes[index] = index;
        }
        int index;
        while (true) {
            kCombinations(source, indexes, combiner);

            index = k - 1;
            while (index >= 0 && indexes[index] == source.size() - k + index) {
                --index;
            }
            if (index < 0) {
                break;
            }
            indexes[index]++;
            for (++index; index < k; ++index) {
                indexes[index] = indexes[index - 1] + 1;
            }
        }
    }

    private static <E> void kCombinations(final List<E> source, final int[] indexes, final Consumer<List<E>> combiner) {
        final ArrayList<E> combination = new ArrayList<>(indexes.length);
        for (final int index : indexes) {
            combination.add(source.get(index));
        }
        combiner.accept(combination);
    }
}
