package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.view.ViewVerifySystem;

/**
 * Controls interactions between {@link ViewVerifySystem} instance and model.
 */
public class ControllerVerifySystem implements Controller<ViewVerifySystem> {
    @Override
    public void register(final Navigator navigator, final ViewVerifySystem view) {

    }

    @Override
    public Class<ViewVerifySystem> viewClass() {
        return ViewVerifySystem.class;
    }
}
