package com.armineasy.injection.interfaces;

/**
 *
 * @author GedMarc
 * @param <M> The module to bind
 */
@FunctionalInterface
public interface DefaultBinder<M>
{

    /**
     * Performs the binding with the injection module that is required
     *
     * @param module
     */
    public void onBind(M module);
}
