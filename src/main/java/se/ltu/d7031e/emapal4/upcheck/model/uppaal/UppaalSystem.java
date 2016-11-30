package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicObject;

/**
 * Represents some UPPAAL system.
 */
public class UppaalSystem {
    private final DynamicObject uppaalSystem;

    UppaalSystem(final DynamicObject uppaalSystem) {
        this.uppaalSystem = uppaalSystem;
    }

    /**
     * @return wrapped dynamic object
     */
    DynamicObject dynamicObject() {
        return uppaalSystem;
    }
}
