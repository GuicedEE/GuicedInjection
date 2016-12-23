package com.armineasy.injection.interfaces;

/**
 *
 * @author GedMarc
 * @param <M> The module to bind
 */
public interface DefaultBinder<M>
{
    public void onBind(M module);
}
