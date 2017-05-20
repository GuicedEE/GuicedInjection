/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.armineasy.injection;

import com.armineasy.injection.abstractions.GuiceInjectorModule;
import com.armineasy.injection.interfaces.GuiceDefaultBinder;

/**
 *
 * @author GedMarc
 */
public class GuiceTestBinder extends GuiceDefaultBinder{

    @Override
    public void onBind(GuiceInjectorModule module) {
        System.out.println("Binding");
    }
    
}
