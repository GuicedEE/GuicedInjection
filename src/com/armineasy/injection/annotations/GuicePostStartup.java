/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.armineasy.injection.annotations;

/**
 * Executes immediately after Guice has been initialized
 *
 * @author Marc Magon
 * @since 15 May 2017
 */
public interface GuicePostStartup
{

    /**
     * Runs immediately after the post load
     */
    public void postLoad();

    /**
     * Sets the order in which this must run, default 100.
     *
     * @return
     */
    public Integer sortOrder();
}
