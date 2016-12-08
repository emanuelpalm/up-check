package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.system.UppaalSystem;

import java.util.List;
import java.util.function.Predicate;

/**
 * TODO: Implement.
 */
public class UppaalSystemFixerStrategyRemoveTransitions implements UppaalSystemFixerStrategy {
    @Override
    public List<UppaalSystemModification> apply(UppaalSystem system, Predicate<UppaalSystem> predicate) {
        throw new IllegalStateException("Not implemented");
    }
}
