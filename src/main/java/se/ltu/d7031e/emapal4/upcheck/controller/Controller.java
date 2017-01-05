package se.ltu.d7031e.emapal4.upcheck.controller;

import se.ltu.d7031e.emapal4.upcheck.view.View;

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
     * @return Class of controlled {@link View}.
     */
    Class<V> viewClass();
}
