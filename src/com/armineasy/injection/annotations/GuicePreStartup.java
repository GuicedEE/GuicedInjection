/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.armineasy.injection.annotations;

/**
 * Initializes before Guice has been injected
 *
 * @author Marc Magon
 * @since 15 May 2017
 */
public interface GuicePreStartup
{

    /**
     * Runs on startup
     */
    public void onStartup();

    /**
     * Sort order for startup, Default 100.
     *
     * @return
     */
    public Integer sortOrder();
}
