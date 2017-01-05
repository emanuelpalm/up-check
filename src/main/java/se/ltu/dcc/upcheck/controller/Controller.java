package se.ltu.dcc.upcheck.controller;

import se.ltu.dcc.upcheck.view.View;

/**
 * An object that can act as an MVC controller in relation to one particular kind of {@link View}.
 */
interface Controller<V extends View> {
    /**
     * Registers given {@link View}.
     * <p>
     * The method provides opportunity for this object to listen for relevant {@link View} events, as well as save the
     * {@link View} reference in order to use it to push GUI changes later on.
     *
     * @param view View to register.
     */
    void register(final Navigator navigator, final V view);

    /**
     * Called to indicate that controller has been disassociated from a previously registered view.
     */
    default void unregister() {}

    /**
     * @return Class of controlled {@link View}.
     */
    Class<V> viewClass();
}
