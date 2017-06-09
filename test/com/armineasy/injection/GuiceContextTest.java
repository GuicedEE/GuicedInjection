/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.armineasy.injection;

import com.armineasy.injection.abstractions.GuiceInjectorModule;
import com.armineasy.injection.interfaces.GuiceDefaultBinder;
import com.armineasy.injection.interfaces.GuiceSiteBinder;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author GedMarc
 */
public class GuiceContextTest extends GuiceDefaultBinder
{

    @BeforeClass
    public static void pre()
    {
        LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);
    }

    @Override
    public void onBind(GuiceInjectorModule module)
    {
        System.out.println("Binding Test");
    }

    @Test
    public void testReflection()
    {
        GuiceContext.reflect();
    }

    @Test
    public void testInjection()
    {
        GuiceContext.inject();
    }

    public static void main(String[] args)
    {

        GuiceContext.inject();

        GuiceContext.reflect().getSubTypesOf(GuiceDefaultBinder.class);
        GuiceContext.reflect().getSubTypesOf(GuiceSiteBinder.class);
    }

}
