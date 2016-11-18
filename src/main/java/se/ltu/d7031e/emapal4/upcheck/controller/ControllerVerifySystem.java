package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.view.ViewVerifySystem;

/**
 * Controls interactions between {@link ViewVerifySystem} instance and model.
 */
public class ControllerVerifySystem implements Controller<ViewVerifySystem> {
    private final UppaalProxy uppaalProxy;

    /**
     * @param uppaalProxy Local UPPAAL installation proxy used to perform verifications.
     */
    public ControllerVerifySystem(final UppaalProxy uppaalProxy) {
        this.uppaalProxy = uppaalProxy;
    }

    @Override
    public void register(final Navigator navigator, final ViewVerifySystem view) {

    }

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
