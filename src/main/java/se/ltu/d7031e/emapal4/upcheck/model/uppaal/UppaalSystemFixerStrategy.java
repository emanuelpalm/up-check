package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.system.UppaalSystem;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a strategy employable in modifying some faulty {@link UppaalSystem} in order to make it correct.
 */
public interface UppaalSystemFixerStrategy {
    /**
     * Applies strategy to given {@link UppaalSystem}, using provided predicate to test for successful applications.
     *
     * @param system    system to modify
     * @param predicate test determining if some modification was valid
     * @return list of successful modifications
     */
    List<UppaalSystemModification> apply(final UppaalSystem system, final Predicate<UppaalSystem> predicate);
}
